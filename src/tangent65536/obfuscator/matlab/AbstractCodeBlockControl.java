package tangent65536.obfuscator.matlab;

public abstract class AbstractCodeBlockControl extends CodeBlock
{
	public final Statement control;
	
	public AbstractCodeBlockControl(Statement clause, Statement.Type[] checks)
	{
		this.control = clause;
		for(Statement.Type type : checks)
		{
			if(clause.type == type)
			{
				return;
			}
		}
		System.err.println(String.format("WARNING: Creating an %s block with a NON-%s (%s) control clause!", checks[0], checks[0], clause.type));
	}
	
	public AbstractCodeBlockControl(Statement clause, Statement.Type check)
	{
		this.control = clause;
		if(clause.type != check)
		{
			System.err.println(String.format("WARNING: Creating an %s block with a NON-%s (%s) control clause!", check, check, clause.type));
		}
	}
	
	// @Override
	// protected abstract IIndexableBlock forceTransform(ArrayList<AbstractCodeBlock> outputs, HashSet<AbstractCodeBlock> traversed);
}
