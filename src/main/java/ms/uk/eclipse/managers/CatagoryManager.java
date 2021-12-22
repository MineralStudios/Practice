package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.SaveableData;

public class CatagoryManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("catagory.yml", "plugins/Practice");
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	GlueList<Catagory> list = new GlueList<>();

	public void registerCatagory(Catagory catagory) {
		list.add(catagory);
	}

	public void remove(Catagory catagory) {
		list.remove(catagory);

		for (Queuetype queuetype : queuetypeManager.getQueuetypes()) {
			queuetype.getCatagories().remove(catagory);
		}
	}

	public boolean contains(Catagory catagory) {
		for (Catagory g : list) {
			if (g.equals(catagory)) {
				return true;
			}
		}
		return false;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Catagory> getCatagorys() {
		return list;
	}

	public Catagory getCatagoryByName(String string) {
		for (Catagory g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public void save() {

		for (Catagory catagory : getCatagorys()) {
			catagory.save();
		}

		config.save();

	}

	@Override
	public void load() {
		try {
			for (String key : getConfig().getConfigurationSection("Catagory.").getKeys(false)) {

				if (key == null) {
					continue;
				}

				Catagory catagory = new Catagory(key);

				catagory.load();

				registerCatagory(catagory);
			}
		} catch (Exception e) {
		}
	}
}
