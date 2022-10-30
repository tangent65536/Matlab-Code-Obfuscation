package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class CodeBlockTransformedDispatcher extends AbstractCodeBlockTransformed
{
	private final ArrayList<IIndexableBlock> blocks = new ArrayList<>();
	
	public void setContents(Collection<IIndexableBlock> contents)
	{
		this.blocks.addAll(contents);
	}
	
	@Override
	public String toString(int level)
	{
		HashSet<IIndexableBlock> serializedBlocks = new HashSet<>();
		HashSet<Integer> usedIds = new HashSet<>();
		
		String indent_0 = getIndent(level);
		String indent_1 = getIndent(level + 1);
		String indent_2 = getIndent(level + 2);
		String indent_3 = getIndent(level + 3);
		
		String dummyVar = String.format("dispatcherDummyVar%04d", level);
		
		String ret = indent_0 + "while true\r\n";
		ret += indent_1 + "switch " + dummyVar + "\r\n";
		HashSet<IIndexableBlock> inxededBlocks = new HashSet<>();

		level += 3;
		HashSet<String> segments = new HashSet<>();
		for(IIndexableBlock block : this.blocks)
		{
			AbstractCodeBlock delegate = (AbstractCodeBlock)block;
			if(block != null && !serializedBlocks.contains(block))
			{
				if(block instanceof IDelegateBlock)
				{
					block = ((IDelegateBlock)block).getEntity();
				}
				
				if(!inxededBlocks.contains(block))
				{
					block.setId(usedIds);
					inxededBlocks.add(block);
				}
				
				String segment = indent_2 + "case " + block.getId() + "\r\n";
				
				// Specifically for "IF/FOR/WHILE/TRY" blocks
				block.updateChain(inxededBlocks, usedIds);
				
				segment += delegate.toString(level);
				segment += block.transformedNextString(level, dummyVar);
				
				// persistent blocks should be at the early stage due to matlab compiler lock
				if(segment.contains(" persistent "))
				{
					ret += segment;
				}
				else
				{
					segments.add(segment);
				}
				
				serializedBlocks.add(block);
			}
		}
		
		for(String segment : segments)
		{
			ret += segment;
		}
		
		for(IIndexableBlock block : this.blocks)
		{
			if(block != null && !(block instanceof IDelegateBlock) && ((AbstractCodeBlock)block).isLeading())
			{
				ret = indent_0 + dummyVar + " = " + block.getId() + ";\r\n" + ret;
				// break;
			}
		}
		
		// Break out the dispatcher block
		ret += indent_2 + "otherwise\r\n";
		ret += indent_3 + "break;\r\n";
		ret += indent_1 + "end\r\n"; // End of "switch"
		ret += indent_0 + "end\r\n"; // End of "while"
		return ret;
	}
}
