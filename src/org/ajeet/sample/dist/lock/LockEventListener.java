package org.ajeet.sample.dist.lock;

public class LockEventListener {
	private final LockManager manager;
	
	public LockEventListener(LockManager manager){
		this.manager = manager;
	}
	
	public void onMessage(Message msg){		
		System.out.println("Listined Message: " + msg.getSource() + " , " + msg.getContent());
		String[] fields = parseMessage(msg);
		LockEventTypes eventTypes = LockEventTypes.valueOf(fields[0]);
		DistributedLock lock = manager.createLock(fields[1]);
		
		switch(eventTypes){
		case Acquire_Lock:
			lock.acquireRemoteLock(msg.getSource());
			break;
		case Release_Lock:
			lock.releaseRemoteLock(msg.getSource());
			break;
		case Joined_by:
			//Notify all member of cluster that a new member just arrived
			System.out.println("New member just joined the family: " + msg.getContent());
			manager.displayInfo(msg);
			break;
		default:
			throw new RuntimeException("Unknown message: " + fields[0]);
		}
	}

	public String[] parseMessage(Message msg){
		return msg.getContent().split(":");
	}
}
