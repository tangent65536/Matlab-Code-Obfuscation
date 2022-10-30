package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockClassMethods extends AbstractCodeBlockClassMembers
{
	private boolean finalized = false;
	
	public CodeBlockClassMethods(Statement clause)
	{
		super(clause, Statement.Type.CLASS_METHODS);
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
