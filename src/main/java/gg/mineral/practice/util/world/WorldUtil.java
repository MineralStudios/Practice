package gg.mineral.practice.util.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import gg.mineral.api.collection.GlueList;

public class WorldUtil {

	static GlueList<String> ignore = new GlueList<>(Arrays.asList("uid.dat", "session.dat"));

	public static void copyWorld(File source, File target) {
		try {

			if (ignore.contains(source.getName())) {
				return;
			}

			if (source.isDirectory()) {
				if (!target.exists()) {
					target.mkdirs();
				}

				String files[] = source.list();

				for (String file : files) {
					File srcFile = new File(source, file);
					File destFile = new File(target, file);
					copyWorld(srcFile, destFile);
				}

				return;
			}

			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(target);

			try {
				byte[] buffer = new byte[1024];
				int length;

				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} finally {
				in.close();
				out.close();
			}

		} catch (Exception e) {

		}
	}

	public static void deleteWorld(World world) {
		Bukkit.unloadWorld(world, false);

		try {
			FileUtils.deleteDirectory(new File(world.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
