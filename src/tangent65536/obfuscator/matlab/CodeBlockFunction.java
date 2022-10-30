package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockFunction extends AbstractCodeBlockControl implements IDefinitionBlock
{
	public CodeBlockFunction(Statement clause)
	{
		super(clause, Statement.Type.FUNCTION);
	}
	
	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control + "\r\n";
		ret += super.toString(level + 1);
		ret += indent + "end\r\n";
		
		return ret;
	}
	
	public IIndexableBlock forceTransform(ArrayList<IIndexableBlock> dummy0, HashSet<AbstractCodeBlock> dummy1)
	{
		CodeBlockTransformedFunction ret = new CodeBlockTransformedFunction(this);
		
		boolean largeEnough = false;
		int count = 0;
		for(AbstractCodeBlock block : this.children)
		{
			if(!(block instanceof IIndexableBlock))
			{
				largeEnough = true;
				break;
			}
			
			count += block.getLinesCount();
			if(count >= 4)
			{
				largeEnough = true;
				break;
			}
		}
		
		if(largeEnough)
		{
			ArrayList<IIndexableBlock> _outputs = new ArrayList<>();
			HashSet<AbstractCodeBlock> _traversed = new HashSet<>();
			
			this.leading = true;
			super.forceTransform(_outputs, _traversed);
			
			HashSet<IIndexableBlock> cache = new HashSet<>();
			for(IIndexableBlock block : _outputs)
			{
				if(cache.contains(block))
				{
					continue;
				}
				cache.add(block);
			}
			
			CodeBlockTransformedDispatcher content = new CodeBlockTransformedDispatcher();
			content.setContents(cache);
			
			ret.children.add(content);
		}
		else
		{
			ret.children.addAll(this.children);
		}
		return ret;
	}
}
