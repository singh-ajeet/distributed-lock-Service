package org.ajeet.sample.dist.lock;


public class DistributedLock {
	private final LockManager manager;
	
	private final String lockName;
	private boolean isLOcked;
	private String lockedBy;
	private final String localhost;
	
	public DistributedLock(String lockName, LockManager manager) {
		this.lockName = lockName;
		this.manager = manager;
		this.localhost = manager.getLocalhost();
	}
	
	public String getLockName() {
		return lockName;
	}
	
	public boolean acquireLock(){
		boolean success = false;
		if(!isLOcked){
			synchronized (this) {
				if(!isLOcked){
					isLOcked = true;
					lockedBy = "localhost";
					success = true;
					String msg = "Acquire_Lock" + ":" + lockName;
					manager.notifyAllRemoteListeners(new Message(msg, localhost));
				}
			}
		}
		return success;
	}

	public void releaseLock(){
		synchronized (this) {
			if(lockedBy.equalsIgnoreCase("localhost")){
				isLOcked = false;
				lockedBy = null;
				String msg = "Release_Lock" + ":" + lockName;
				manager.notifyAllRemoteListeners(new Message(msg, localhost));
			}
		}
	}
	
	public boolean acquireRemoteLock(String source){
		boolean success = false;
		if(!isLOcked){
			synchronized (this) {
				if(!isLOcked){
					isLOcked = true;
					lockedBy = source;
					success = true;	
				}
			}
		} else if(lockedBy.equalsIgnoreCase(source)){
			success = true;
		}
		return success;
	}

	public void releaseRemoteLock(String source){
		synchronized (this) {
			if(lockedBy.equalsIgnoreCase(source)){
				isLOcked = false;
				lockedBy = null;
			}
		}
	}
	
}
