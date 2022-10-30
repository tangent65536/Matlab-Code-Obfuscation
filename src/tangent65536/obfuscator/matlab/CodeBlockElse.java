package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockElse extends AbstractCodeBlockControl
{
	/**
	 * @param clause The corresponding "if" or "elseif" clause
	 */
	public CodeBlockElse(Statement clause)
	{
		super(clause, Statement.Type.ELSE);
	}

	@Override
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		return super.forceTransform(outputs, traversed);
	}
}
