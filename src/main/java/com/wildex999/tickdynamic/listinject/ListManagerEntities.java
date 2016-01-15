package com.wildex999.tickdynamic.listinject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.wildex999.tickdynamic.TickDynamicMod;

import net.minecraft.world.World;

//The World Entities loop does not use iterators, so we have to handle it specially

public class ListManagerEntities extends ListManager {

	private boolean updateStarted;
	private EntityIteratorTimed entityIterator;
	private EntityObject lastObj;
	
	private CustomProfiler profiler;
	
	public ListManagerEntities(World world, TickDynamicMod mod) {
		super(world, mod, EntityType.Entity);
		profiler = (CustomProfiler)world.theProfiler;
	}
	
	public void stopUpdate() {
		updateStarted = false;
		if(entityIterator != null)
			entityIterator.endUpdate();
	}
	
	@Override
	public int size() {
		if(profiler.stage == CustomProfiler.Stage.None || profiler.stage == CustomProfiler.Stage.InTick || profiler.stage == CustomProfiler.Stage.BeforeLoop)
			return super.size();

		if(!updateStarted) {
			updateStarted = true;
			
			entityIterator = new EntityIteratorTimed(this, this.getAge());
		}
		
		//Verify we have a next element to move on to
		if(!entityIterator.hasNext())
		{
			updateStarted = false;
			return 0; //Should end
		}

		return super.size();
	}
	
	@Override
	public EntityObject get(int index) {
		if(!updateStarted || profiler.stage == CustomProfiler.Stage.InTick)
			return super.get(index);
		
		lastObj = entityIterator.next();
		return lastObj;
	}
	
	@Override
	public EntityObject remove(int index) {
		if(!updateStarted || profiler.stage != CustomProfiler.Stage.InRemove)
			return super.remove(index);
		
		//Fast remove the current Entity
		entityIterator.remove();
		return lastObj;
	}
	
	//Iterators: Return non-timed(For now. Later we might to return timed if they start using iterators)
	@Override
	public ListIterator<EntityObject> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<EntityObject> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Iterator<EntityObject> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
