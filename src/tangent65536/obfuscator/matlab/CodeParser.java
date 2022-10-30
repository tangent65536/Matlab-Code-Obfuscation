package tangent65536.obfuscator.matlab;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Stack;

public class CodeParser
{
	public static CodeBlock parseFile(BufferedReader reader, boolean obfuscate) throws Exception
	{
		String line;
		boolean comment = false;
		
		Stack<ParserStackEntry> stack = new Stack<>();
		
		// The "genesis" empty block containing the "next" pointer to the fill script chain
		CodeBlock genesis = new CodeBlock();
		ParserStackEntry globalScope = new ParserStackEntry(genesis);
		// globalScope.tails.add(genesis); // Set the dangling attach point
		stack.push(globalScope);
		
		ArrayList<Statement> cachedLines = new ArrayList<>();
		
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			if(line.length() == 0)
			{
				continue; // Blank line
			}
			
			if(line.startsWith("%")) // Line comment
			{
				if(comment)
				{
					if(line.equals("%}"))
					{
						comment = false;
					}
				}
				else
				{
					if(line.equals("%{"))
					{
						comment = false;
					}
				}
				continue;
			}
			
			Statement clause = Statement.parseStatement(line);
			
			if(clause.type == Statement.Type.DEFAULT)
			{
				cachedLines.add(clause);
				continue;
			}
			
			// Flush the cached lines to a new block.
			if(!cachedLines.isEmpty())
			{
				CodeBlockScript chunk = new CodeBlockScript();
				chunk.addStatements(cachedLines);
				
				stack.peek().appendChokingBlock(chunk);
				cachedLines.clear();
			}
			
			switch(clause.type)
			{
				case END:
				{
					// The level that's ending
					ParserStackEntry endingScope = stack.pop();
					
					// The level we are going back to
					ParserStackEntry wrappingScope = stack.peek();
					
					if(obfuscate)
					{
						if(endingScope.parent instanceof ILoopBlock)
						{
							endingScope.setLoopBack();
						}
					}

					if(endingScope.parent instanceof IDefinitionBlock)
					{
						endingScope.cutTails();
					}
					
					wrappingScope.addDanglingTails(endingScope.tails);
					
					break;
				}
				case IF:
				{
					CodeBlockIf ifBlock = new CodeBlockIf(clause);
					stack.peek().appendChokingBlock(ifBlock);
					stack.push(new ParserStackEntry(ifBlock));
					
					break;
				}
				case ELSEIF:
				{
					/* 
					 * No need to add this block to the parent container!
					 *  It's already linked to the "IF" block as the "elseBlock" like a tree node!
					 */
					
					// The preceding "if" that's ending
					ParserStackEntry endingScope = stack.pop();
					
					// The level above this "elseif" statement and its sub-block
					ParserStackEntry wrappingScope = stack.peek();
					
					wrappingScope.addDanglingTails(endingScope.tails);
					
					CodeBlockIf ifBlock = new CodeBlockIf(clause);
					stack.push(new ParserStackEntry(ifBlock));
					
					wrappingScope.branchTail(ifBlock);
					((CodeBlockIf)endingScope.parent).setElse(ifBlock);
					
					break;
				}
				case ELSE:
				{
					/* 
					 * No need to add this block to the parent container!
					 *  It's already linked to the "IF" block as the "elseBlock" like a tree node!
					 */
					
					// The preceding "if"/"elseif" that's ending
					ParserStackEntry endingScope = stack.pop();
					
					// The level above this "else" statement and its sub-block
					ParserStackEntry wrappingScope = stack.peek();
					
					wrappingScope.addDanglingTails(endingScope.tails);
					
					CodeBlockElse elseBlock = new CodeBlockElse(clause);
					stack.push(new ParserStackEntry(elseBlock));
					
					wrappingScope.branchTail(elseBlock);
					((CodeBlockIf)endingScope.parent).setElse(elseBlock);
					
					break;
				}
				case FOR:
				{
					CodeBlockFor forBlock = new CodeBlockFor(clause);
					stack.peek().appendChokingBlock(forBlock);
					stack.push(new ParserStackEntry(forBlock));
					
					break;
				}
				case WHILE:
				{
					CodeBlockWhile whileBlock = new CodeBlockWhile(clause);
					stack.peek().appendChokingBlock(whileBlock);
					stack.push(new ParserStackEntry(whileBlock));
					
					break;
				}
				case TRY:
				{
					CodeBlockTry tryBlock = new CodeBlockTry(clause);
					stack.peek().appendChokingBlock(tryBlock);
					stack.push(new ParserStackEntry(tryBlock));
					
					break;
				}
				case CATCH:
				{
					// The preceding "try" that's ending
					ParserStackEntry endingScope = stack.pop();
					
					// The level above this "catch" statement and its sub-block
					ParserStackEntry wrappingScope = stack.peek();
					
					wrappingScope.addDanglingTails(endingScope.tails);
					
					CodeBlockCatch catchBlock = new CodeBlockCatch(clause);
					stack.push(new ParserStackEntry(catchBlock));
					
					((CodeBlockTry)endingScope.parent).setCatch(catchBlock);
					
					break;
				}
				case BREAK:
				{
					System.err.println("BREAK IMPLEMENTATION NOT TESTED!");
					
					CodeBlockBreak breakBlock = new CodeBlockBreak(Statement.Type.BREAK);
					stack.peek().appendChokingBlock(breakBlock);
					
					Stack<ParserStackEntry> cache = new Stack<>();
					ParserStackEntry ptr;
					do
					{
						ptr = stack.pop();
						cache.push(ptr);
					}
					while(!(ptr.parent instanceof ILoopBlock));
					stack.peek().branchTail(breakBlock);
					
					while(!cache.isEmpty())
					{
						stack.push(cache.pop());
					}
					
					break;
				}
				case CONTINUE:
				{
					CodeBlockBreak breakBlock = new CodeBlockBreak(Statement.Type.CONTINUE);
					stack.peek().appendChokingBlock(breakBlock);
					
					Stack<ParserStackEntry> cache = new Stack<>();
					ParserStackEntry ptr;
					do
					{
						ptr = stack.pop();
						cache.push(ptr);
					}
					while(!(ptr.parent instanceof ILoopBlock));
					breakBlock.setNextBlock(((ILoopBlock)ptr.parent).getLoopbackBlock());
					
					while(!cache.isEmpty())
					{
						stack.push(cache.pop());
					}
					
					break;
				}
				case FUNCTION:
				{
					CodeBlockFunction functionBlock = new CodeBlockFunction(clause);
					stack.peek().appendChokingBlock(functionBlock);
					stack.push(new ParserStackEntry(functionBlock));
					
					break;
				}
				case CLASSDEF:
				{
					CodeBlockClassDef classDefBlock = new CodeBlockClassDef(clause);
					stack.peek().appendChokingBlock(classDefBlock);
					stack.push(new ParserStackEntry(classDefBlock));
					
					break;
				}
				case CLASS_PROPERTIES:
				{
					CodeBlockClassProperties classPropsBlock = new CodeBlockClassProperties(clause);
					stack.peek().appendChokingBlock(classPropsBlock);
					stack.push(new ParserStackEntry(classPropsBlock));
					
					break;
				}
				case CLASS_METHODS:
				{
					CodeBlockClassMethods classMethodsBlock = new CodeBlockClassMethods(clause);
					stack.peek().appendChokingBlock(classMethodsBlock);
					stack.push(new ParserStackEntry(classMethodsBlock));

					break;
				}
				default:
				{
					throw new Exception("STATEMENT NOT IMPLEMENTED!");
				}
			}
		}
		
		ParserStackEntry master = stack.pop();
		if(!cachedLines.isEmpty())
		{
			CodeBlockScript finalBlock = new CodeBlockScript();
			finalBlock.addStatements(cachedLines);
			master.appendChokingBlock(finalBlock);
		}
		
		return master.parent;
	}
	
	public static class ParserStackEntry
	{
		private final ArrayList<AbstractCodeBlock> tails = new ArrayList<>();
		private final CodeBlock parent;
		
		ParserStackEntry(CodeBlock base)
		{
			this.parent = base;
		}
		
		protected void addDanglingTails(ArrayList<AbstractCodeBlock> moreTails)
		{
			this.tails.addAll(moreTails);
		}
		
		// Add block to the list but no new tail. For "break" statement only.
		protected void appendChokingBlock(CodeBlockBreak block)
		{
			for(AbstractCodeBlock cb : this.tails)
			{
				// Attach the new block to all the dangling "mounting point" at the same level.
				cb.setNextBlock(block);
			}
			this.tails.clear();
			
			// Register this block
			this.parent.addChild(block);
		}
		
		// Append a block that attaches to all dangling tail in the current scope level,
		//  and set the new dangling tail in the scope to the single new block
		protected void appendChokingBlock(AbstractCodeBlock tail)
		{
			for(AbstractCodeBlock cb : this.tails)
			{
				// Attach the new block to all the dangling "mounting point" at the same level.
				cb.setNextBlock(tail);
			}
			this.tails.clear();
			
			// Register this block and set it as the new tail
			this.appendBlock(tail);
		}
		
		// Add block to the list and a new tail branch
		protected void appendBlock(AbstractCodeBlock tail)
		{
			this.parent.addChild(tail);
			this.tails.add(tail);
		}
		
		// Add a new tail branch and does nothing else!
		protected void branchTail(AbstractCodeBlock tail)
		{
			this.tails.add(tail);
		}
		
		// For obfuscation, mark all tail blocks at the end of a loop block (e.g. "while")
		//  to point to the start of that loop block, forming an actual "loop".
		protected void setLoopBack()
		{
			for(AbstractCodeBlock cb : this.tails)
			{
				cb.setNextBlock(((ILoopBlock)this.parent).getLoopbackBlock());
			}
			this.tails.clear();
		}
		
		// At the end of any definition blocks, the tail should be finalized as empty (end of function/definition).
		protected void cutTails()
		{
			this.tails.clear();
		}
	}
}
