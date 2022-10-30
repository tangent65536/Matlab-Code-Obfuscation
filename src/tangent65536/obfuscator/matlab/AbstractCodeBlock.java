package tangent65536.obfuscator.matlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public abstract class AbstractCodeBlock
{
	protected AbstractCodeBlock next = null;
	
	protected IIndexableBlock transformedHead = null;
	
	protected boolean leading = true;
	
	private static final Random RAND = new Random();
	
	public final void setNextBlock(AbstractCodeBlock _next)
	{
		this.next = _next;
		if(_next != null)
		{
			_next.markNotLeading();
		}
	}
	
	public boolean isLeading()
	{
		return this.leading;
	}
	
	public void markNotLeading()
	{
		this.leading = false;
	}
	
	protected abstract String toString(int level);
	
	/*
	public String toString(AbstractCodeBlock next, int level)
	{
		String ret = this.toString(level);
		
		if(this.next != next && this.next != null)
		{
			ret += this.next.toString(null, level);
		}
		
		return ret;
	}
	*/
	
	public static String getIndent(int level)
	{
		if(level <= 0)
		{
			return "";
		}
		return new String(new char[level << 2]).replaceAll("\0", " ");
	}
	
	protected abstract IIndexableBlock forceTransform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed);
	
	public final IIndexableBlock transform(ArrayList<IIndexableBlock> outputs, HashSet<AbstractCodeBlock> traversed)
	{
		if(this instanceof IIndexableBlock)
		{
			IIndexableBlock ret = (IIndexableBlock)this;
			if(ret.finalized(traversed))
			{
				return ret;
			}
		}
		
		if(!traversed.contains(this))
		{
			this.transformedHead = this.forceTransform(outputs, traversed);
			outputs.add(this.transformedHead);
			traversed.add(this);
		}
		
		return this.transformedHead;
	}
	
	public IIndexableBlock getLastTransformed()
	{
		if(this instanceof IIndexableBlock)
		{
			return (IIndexableBlock)this;
		}
		return this.transformedHead;
	}
	
	public static int getUniqueU16(HashSet<Integer> blocked)
	{
		int ret;
		do
		{
			ret = RAND.nextInt(IIndexableBlock.INVALID_ID);
		}
		while(blocked.contains(ret));
		blocked.add(ret);
		return ret;
	}
	
	public abstract int getLinesCount();
}
