package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.SaveableData;

public class GametypeManager implements SaveableData {
	FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	GlueList<Gametype> list = new GlueList<>();

	public void registerGametype(Gametype gametype) {
		list.add(gametype);
	}

	public void remove(Gametype gametype) {
		list.remove(gametype);

		for (Catagory catagory : catagoryManager.getCatagorys()) {
			catagory.getGametypes().remove(gametype);
		}

		for (Queuetype queuetype : queuetypeManager.getQueuetypes()) {
			queuetype.getGametypes().remove(gametype);
		}
	}

	public boolean contains(Gametype gametype) {
		for (Gametype g : list) {
			if (g.equals(gametype)) {
				return true;
			}
		}
		return false;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Gametype> getGametypes() {
		return list;
	}

	public Gametype getGametypeByName(String string) {
		for (Gametype g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public void save() {

		for (Gametype gametype : getGametypes()) {
			gametype.save();
		}

		config.save();

	}

	@Override
	public void load() {
		try {
			for (String key : getConfig().getConfigurationSection("Gametype.").getKeys(false)) {

				if (key == null) {
					continue;
				}

				Gametype gametype = new Gametype(key);

				gametype.load();

				registerGametype(gametype);
			}
		} catch (Exception e) {
		}

	}
}
