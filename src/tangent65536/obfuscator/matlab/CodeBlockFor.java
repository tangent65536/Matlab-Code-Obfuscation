package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;

public class CodeBlockFor extends AbstractCodeBlockControl implements ILoopBlock
{
	private final CodeBlockPseudoLoopback pseudoBlock = new CodeBlockPseudoLoopback(this);
	
	public CodeBlockFor(Statement clause)
	{
		super(clause, Statement.Type.FOR);
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
		/* Parse current for statement */
		
		// Currently only explicit array declaration is support, e.g. "i = a:b" or "i = a:2:b"
		String[] statements = this.control.statement.substring(3).trim().split(":");
		
		String init = statements[0].trim() + ";";
		String loopVar = init.split("=")[0].trim();
		boolean incrementSign = true;
		String increment;
		String condition;
		
		if(statements.length > 2)
		{
			increment = statements[1];
			incrementSign = Double.parseDouble(increment) > 0;
			condition = statements[2];
		}
		else
		{
			increment = "1";
			condition = statements[1];
		}
		condition = loopVar + (incrementSign ? " <= " : " >= ") + condition.trim().split("%")[0];
		
		// The loop initialization block, setting the loop variable to the starting value.
		CodeBlockScript ret = new CodeBlockScript();
		ret.addStatement(Statement.parseStatement(init));
		
		// After the stuffs in the for loop, increment the loop variable by an increment.
		//  This has to be chained to the end of the inner tail blocks.
		//  Therefore, THIS is the pointer which the loopback SHOULD point to!
		CodeBlockScript incrementLine = new CodeBlockScript();
		incrementLine.addStatement(Statement.parseStatement(loopVar + " = " + loopVar + " + " + increment + ";"));
		this.pseudoBlock.setTransformedLoopbackPtr(incrementLine);
		
		// The actual conditional handler
		CodeBlockTransformedIf loop = new CodeBlockTransformedIf(Statement.parseStatement("if " + condition), super.forceTransform(outputs, traversed), this.next == null ? null : this.next.transform(outputs, traversed));
		
		// Chain the initialization statement block AND the increment loopback block to the actual loop condition handler
		ret.setNextBlock(loop);
		incrementLine.setNextBlock(loop);
		
		// Add the branched blocks to the list
		outputs.add(incrementLine);
		outputs.add(loop);
		
		if(!this.leading)
		{
			ret.markNotLeading();
		}
		incrementLine.markNotLeading();
		
		return ret;
	}

	@Override
	public CodeBlockPseudoLoopback getLoopbackBlock()
	{
		return this.pseudoBlock;
	}
	
	public int getLinesCount()
	{
		return super.getLinesCount() + 3;
	}
}
