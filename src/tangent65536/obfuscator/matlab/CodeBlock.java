package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlock extends AbstractCodeBlock
{
	protected final ArrayList<AbstractCodeBlock> children = new ArrayList<>();
	
	public void addChild(AbstractCodeBlock code)
	{
		this.children.add(code);
	}
	
	public void addChildren(ArrayList<AbstractCodeBlock> codes)
	{
		this.children.addAll(codes);
	}
	
	public String toString()
	{
		return this.toString(0);
	}
	
	protected String toString(int level)
	{
		String ret = "";
		for(AbstractCodeBlock block : this.children)
		{
			ret += block.toString(level);
		}
		return ret;
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		if(this.children.isEmpty())
		{
			CodeBlockTransformedBlank blank = new CodeBlockTransformedBlank();
			blank.setNextBlock(this.next);
			return blank;
		}
		
		IIndexableBlock transformedHead = this.children.get(0).transform(outputs, traversed);
		if(!this.leading)
		{
			((AbstractCodeBlock)transformedHead).markNotLeading();
		}
		
		for(int i = 1 ; i < this.children.size() ; i++)
		{
			((AbstractCodeBlock)this.children.get(i).transform(outputs, traversed)).markNotLeading();
		}
		return transformedHead;
	}
	
	public AbstractCodeBlockTransformed transformPlainScript()
	{
		ArrayList<IIndexableBlock> outputs = new ArrayList<>();
		HashSet<AbstractCodeBlock> traversed = new HashSet<>();
		
		this.transform(outputs, traversed);
		
		boolean def = this instanceof IDefinitionBlock;
		for(IIndexableBlock block : outputs)
		{
			if(block instanceof IDefinitionBlock)
			{
				def = true;
				break;
			}
		}
		
		AbstractCodeBlockTransformed ret;
		if(def)
		{
			ret = new AbstractCodeBlockTransformed();
			ret.addTransformedChildren(outputs);
		}
		else
		{
			CodeBlockTransformedDispatcher ret_ = new CodeBlockTransformedDispatcher();
			ret_.setContents(outputs);
			ret = ret_;
		}
		
		return ret;
	}
	
	public int getLinesCount()
	{
		int ret = 0;
		for(AbstractCodeBlock block : this.children)
		{
			ret += block.getLinesCount();
		}
		return ret;
	}
}
