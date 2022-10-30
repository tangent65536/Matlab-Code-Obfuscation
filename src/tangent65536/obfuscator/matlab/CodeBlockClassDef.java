package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockClassDef extends AbstractCodeBlockTransformedControl
{
	private boolean finalized = false;
	
	public CodeBlockClassDef(Statement clause)
	{
		super(clause, Statement.Type.CLASSDEF);
	}
	
	public void addChild(AbstractCodeBlock code)
	{
		if(!(code instanceof AbstractCodeBlockClassMembers))
		{
			System.err.println(String.format("WARNING: Adding non CLASS MEMBER block (%s) to CLASSDEF block!", code.getClass().getSimpleName()));
		}
		super.addChild(code);
	}
	
	public void addChildren(ArrayList<AbstractCodeBlock> codes)
	{
		for(AbstractCodeBlock cb : codes)
		{
			if(!(cb instanceof AbstractCodeBlockClassMembers))
			{
				System.err.println(String.format("WARNING: Adding non CLASS MEMBER block(s) (%s) to CLASSDEF block!", cb.getClass().getSimpleName()));
			}
		}
		super.addChildren(codes);
	}
	
	protected String toString(int level)
	{
		String indent = getIndent(level);
		
		String ret = indent + this.control + "\r\n";
		ret += super.toString(level + 1);
		ret += indent + "end\r\n";
		
		return ret;
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		ArrayList<IIndexableBlock> dummy0 = new ArrayList<>();
		HashSet<AbstractCodeBlock> dummy1 = new HashSet<>();
		for(int i = 0 ; i < this.children.size() ; i++)
		{
			this.children.set(i, (AbstractCodeBlock)this.children.get(i).transform(dummy0, dummy1));
		}
		this.finalized = true;
		
		return this;
	}
	
	public boolean finalized(HashSet<AbstractCodeBlock> traversed)
	{
		return this.finalized;
	}
}
