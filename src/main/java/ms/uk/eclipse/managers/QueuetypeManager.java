package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.SaveableData;

public class QueuetypeManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	final GlueList<Queuetype> list = new GlueList<>();

	public void registerQueuetype(Queuetype queuetype) {
		list.add(queuetype);
	}

	public void remove(Queuetype queuetype) {
		list.remove(queuetype);
	}

	public boolean contains(Queuetype queuetype) {
		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			if (q.equals(queuetype)) {
				return true;
			}
		}
		return false;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Queuetype> getQueuetypes() {
		return list;
	}

	public Queuetype getQueuetypeByName(String string) {
		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			if (q.getName().equalsIgnoreCase(string)) {
				return q;
			}
		}

		return null;
	}

	@Override
	public void save() {

		for (Queuetype queuetype : getQueuetypes()) {
			queuetype.save();
		}

		config.save();
	}

	@Override
	public void load() {
		try {
			for (String key : getConfig().getConfigurationSection("Queue.").getKeys(false)) {

				if (key == null) {
					continue;
				}

				Queuetype queuetype = new Queuetype(key);

				queuetype.load();

				registerQueuetype(queuetype);
			}
		} catch (Exception e) {
		}

	}
}
