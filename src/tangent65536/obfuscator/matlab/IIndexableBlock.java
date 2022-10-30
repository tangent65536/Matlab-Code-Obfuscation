package tangent65536.obfuscator.matlab;

import java.util.HashSet;

public interface IIndexableBlock
{
	public static final int INVALID_ID = 0x10000;
	
	public void setId(HashSet<Integer> used);
	public int getId();
	
	public default boolean finalized(HashSet<AbstractCodeBlock> traversed)
	{
		return true;
	}
	
	public default boolean nextBlockReachable()
	{
		return true;
	}
	
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used);
	
	public String transformedNextString(int level, String loopVarName);
}
