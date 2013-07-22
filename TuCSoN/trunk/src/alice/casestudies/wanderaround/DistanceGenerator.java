package alice.casestudies.wanderaround;

import java.util.ArrayList;

public class DistanceGenerator extends Thread{
	
	private boolean iteraction = true;
	private ArrayList<ISensorEventListener> listeners;
	private NxtSimulatorGUI gui;
	
	public DistanceGenerator(){
		listeners = new ArrayList<ISensorEventListener>();
		gui = NxtSimulatorGUI.getNxtSimulatorGUI();
		this.start();
	}
	
	public void run(){
		while(iteraction){
			try{				
				for( int i=0; i<listeners.size(); i++ ){
					if( listeners.get(i).getListenerName().equals("ultrasonicSensorFront") ){
						listeners.get(i).notifyEvent("distance", gui.getDistance("front"));
					}else if( listeners.get(i).getListenerName().equals("ultrasonicSensorRight") ){
						listeners.get(i).notifyEvent("distance", gui.getDistance("right"));
					}else if( listeners.get(i).getListenerName().equals("ultrasonicSensorBack") ){
						listeners.get(i).notifyEvent("distance", gui.getDistance("back"));
					}else if( listeners.get(i).getListenerName().equals("ultrasonicSensorLeft") ){
						listeners.get(i).notifyEvent("distance", gui.getDistance("left"));
					}
				}
				
				Thread.sleep(1000);
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
	}
	
	public void addListener( ISensorEventListener l ){
		if( !listeners.contains(l) )
			listeners.add(l);
	}
	
	public void removeListener( ISensorEventListener l ){
		if( listeners.contains(l) )
			listeners.remove(l);
	}
}
