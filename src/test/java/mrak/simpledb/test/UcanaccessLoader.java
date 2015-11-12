package mrak.simpledb.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

public class UcanaccessLoader {
	
	private static void _log_(String msg, StringBuilder b) {
		b.append(msg).append("\n");
	}
	
	public static void load() throws Exception {
		if(ok()) return;
		
		final StringBuilder LOG = new StringBuilder();
		
		File projectDir = new File(UcanaccessLoader.class.getResource("/").toURI());
		
		while (!"simple-db".equals(projectDir.getName())) {
			projectDir = projectDir.getParentFile();
		}
		
		File libsDir;
		libsDir = new File(projectDir, "libs");
		libsDir = new File(libsDir, "ucanaccess");
		
		final List<File> jars = new ArrayList<>();
		final List<URL>  jarURLs = new ArrayList<>();
		Files.walkFileTree(libsDir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if(file.getFileName().toString().endsWith(".jar")) {
					jars.add(file.toFile());
					jarURLs.add(file.toFile().toURI().toURL());
				}
				return super.visitFile(file, attrs);
			}
		});
		
		URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> systemClassLoaderClazz = systemClassLoader.getClass();
		Method addUrlMethod;
		//addUrlMethod = systemClassLoaderClazz.getDeclaredMethod("addURL", new Class[] { URL.class });
		//addUrlMethod.setAccessible(true);
		
		URL[] jarsURLsArray = jarURLs.toArray(new URL[jarURLs.size()]);
		for(URL j : jarsURLsArray) {
			_log_("extracted jar " + j, LOG);
			JarFile jar = new JarFile(new File(j.toURI()));
			ZipEntry z = jar.getEntry("net/ucanaccess/jdbc/UcanaccessDriver.class");
			if(z != null) {
				_log_("UcanaccessDriver found! ", LOG);
			}
			
			Logger.getLogger("test").info("jars to add to the path " + j);
			System.out.println(j);
			//addUrlMethod.invoke(systemClassLoader, new Object[] { j.toURI().toURL() });
		}
		
		URLClassLoader loader = new URLClassLoader(jarsURLsArray, systemClassLoader);
		Thread.currentThread().setContextClassLoader(loader);
		//loader.close();
		//loader.loadClass("net.ucanaccess.jdbc.UcanaccessDriver");
		
		if(!ok()) {
			throw new Exception("ERROR LOG:\n" + LOG.toString());
		}
	}
	
	private static boolean ok() {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {
		URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> systemClassLoaderClazz = systemClassLoader.getClass();
		
		for(Method m : systemClassLoaderClazz.getDeclaredMethods()) {
			System.out.println(m);
		}
	}
	
}
