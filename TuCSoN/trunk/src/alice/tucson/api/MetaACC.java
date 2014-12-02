package alice.tucson.api;
import alice.tucson.rbac.RBAC;

public interface MetaACC extends EnhancedACC {

	void add(RBAC rbac);
	void remove(RBAC rbac);
}
