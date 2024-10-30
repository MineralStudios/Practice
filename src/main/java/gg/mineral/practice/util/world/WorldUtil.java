package gg.mineral.practice.util.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import gg.mineral.api.collection.GlueList;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

public class WorldUtil {

	static GlueList<String> ignore = new GlueList<>(Arrays.asList("uid.dat", "session.dat"));

	@SneakyThrows
	public static void copyWorld(File source, File target) {

		if (ignore.contains(source.getName()))
			return;

		if (source.isDirectory()) {
			if (!target.exists())
				target.mkdirs();

			val files = source.list();

			for (val file : files) {
				val srcFile = new File(source, file);
				val destFile = new File(target, file);
				copyWorld(srcFile, destFile);
			}

			return;
		}

		@Cleanup
		val in = new FileInputStream(source);
		@Cleanup
		val out = new FileOutputStream(target);

		val buffer = new byte[1024];
		int length;

		while ((length = in.read(buffer)) > 0)
			out.write(buffer, 0, length);
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
