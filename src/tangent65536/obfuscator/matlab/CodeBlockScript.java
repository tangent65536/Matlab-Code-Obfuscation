package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockScript extends AbstractCodeBlock implements IIndexableBlock
{
	private final ArrayList<Statement> statements = new ArrayList<>();
	
	private int id = INVALID_ID;
	
	public void addStatement(Statement statement)
	{
		if(statement.type != Statement.Type.DEFAULT)
		{
			System.err.println("WARNING: Adding control statement to plain script block!");
		}
		this.statements.add(statement);
	}
	
	public void addStatements(ArrayList<Statement> script)
	{
		for(Statement clause : script)
		{
			this.addStatement(clause);
		}
	}

	@Override
	protected String toString(int level)
	{
		String indent = getIndent(level);
		String ret = "";
		for(Statement clause : this.statements)
		{
			ret += indent + clause + "\r\n";
		}
		return ret;
	}
	
	@Override
	public String transformedNextString(int level, String loopVarName)
	{
		return getIndent(level) + loopVarName + " = " + (this.next == null ? INVALID_ID : this.next.getLastTransformed().getId()) + ";\r\n";
	}
	
	protected IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		if(this.statements.size() > 2)
		{
			// Spawn a new "next" block and insert it into the chain
			CodeBlockScript spawn = new CodeBlockScript();
			spawn.setNextBlock(this.next);
			this.setNextBlock(spawn);
			
			for(int i = 2 ; i < this.statements.size() ; i++)
			{
				spawn.addStatement(this.statements.get(i));
			}
			for(int i = this.statements.size() - 1 ; i >= 2 ; i--)
			{
				this.statements.remove(i);
			}
			
			spawn.transform(outputs, traversed);
		}
		return this;
	}

	@Override
	public void setId(HashSet<Integer> used)
	{
		if(this.id == INVALID_ID)
		{
			this.id = getUniqueU16(used);
		}
	}

	@Override
	public int getId()
	{
		return this.id;
	}
	
	@Override
	public boolean finalized(HashSet<AbstractCodeBlock> traversed)
	{
		return traversed.contains(this);
	}
	
	public void updateChain(HashSet<IIndexableBlock> blocks, HashSet<Integer> used)
	{
		if(this.next != null)
		{
			IIndexableBlock _next = this.next.getLastTransformed();
			
			if(!blocks.contains(_next))
			{
				_next.setId(used);
				blocks.add(_next);
			}
		}
	}
	
	public int getLinesCount()
	{
		return this.statements.size();
	}
}
