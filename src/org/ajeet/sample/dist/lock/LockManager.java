package org.ajeet.sample.dist.lock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LockManager {
	private DistributedService distributedService;
	private final Map<String, DistributedLock> locks = new HashMap<String, DistributedLock>();
	private final String localhost;
	private final String memberid;
	
	public LockManager(String localhost, String memberid) {
		this.localhost = localhost;
		this.memberid = memberid;
	}
	
	public void start() throws IOException{
		distributedService = new DistributedService();
		distributedService.registerListener(new LockEventListener(this));
		distributedService.init();
		String msg = "Joined_by: " + ":" + memberid;
		//To notify all nodes of the network, that a new member just joined the family.
		notifyAllRemoteListeners(new Message(msg, localhost));
	}
	
	public void stop(){
		distributedService.dispose();
	}
	
	/**
	 * It will return existing lock, if it does not exits than it will create new lock.
	 * Through out the network single lock will exist for a name.
	 * @param lockName
	 * @return
	 */
	public DistributedLock createLock(String lockName){
		DistributedLock lock = locks.get(lockName);
		if(lock == null){
			lock = new DistributedLock(lockName, this);
			locks.put(lockName, lock);
		}
		return lock;
	}	

	public String getLocalhost() {
		return localhost;
	}

	public void notifyAllRemoteListeners(Message message) {
		distributedService.sendMessage(message);
		
	}

	public void displayInfo(Message msg){
		if(!msg.getSource().equalsIgnoreCase(localhost)){
			System.out.println(msg.getContent());
		}
		
	}
	
//	public static void main(String[] args) throws IOException, InterruptedException {
//		LockManager manager = new LockManager();
//		manager.start();
//		manager.distributedService.sendMessage(new Message("A1", "localhost"));
//		//Wait to consume above message by receiver.
//		Thread.currentThread().sleep(100);
//		
//		//dispose distributed service
//		manager.stop();
//	}

}
