package tangent65536.obfuscator.matlab;

import java.util.HashSet;

public class CodeBlockTransformedFunction extends CodeBlockFunction implements IIndexableBlock, IDefinitionBlock
{
	public CodeBlockTransformedFunction(CodeBlockFunction base)
	{
		super(base.control);
	}

	@Override
	public void setId(HashSet<Integer> used){}

	@Override
	/**
	 * This is a definition block => return error!
	 */
	public int getId()
	{
		System.err.println("Calling get ID on a definition block!");
		return INVALID_ID;
	}

	@Override
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
		System.err.println("Calling update chain on a definition block!");
	}

	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		return "";
	}
}
