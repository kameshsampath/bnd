package org.osgi.service.indexer;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.*;

import org.osgi.framework.*;
import org.osgi.service.indexer.impl.*;
import org.osgi.service.indexer.impl.types.*;
import org.osgi.service.indexer.impl.util.*;
import org.osgi.service.subsystem.*;

public class SubsystemUtil {

	public static MimeType getMimeType(Resource resource) throws IOException {

		return MimeType.Subsystem;
	}

	public static Manifest getManifest(Resource resource) throws IOException {

		return resource.getManifest();
	}

	public static SymbolicName getSymbolicName(Resource resource) throws IOException {
		Manifest manifest = getManifest(resource);
		if (manifest == null)
			throw new IllegalArgumentException(String.format(
					"Cannot identify symbolic name for resource %s: manifest unavailable", resource.getLocation()));

		String symbolicNameHeader = manifest.getMainAttributes().getValue(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME);

		if (symbolicNameHeader == null)
			throw new IllegalArgumentException(
					"Not an Subsystem archive: missing 'Subsystem-SymbolicName' entry from manifest.");

		Map<String,Map<String,String>> map = OSGiHeader.parseHeader(symbolicNameHeader);
		if (map.size() != 1)
			throw new IllegalArgumentException("Invalid format for Subsystem-SymbolicName header.");

		Entry<String,Map<String,String>> entry = map.entrySet().iterator().next();
		return new SymbolicName(entry.getKey(), entry.getValue());
	}

	public static Map<String,Map<String,String>> getSubsystemContent(Resource resource) throws IOException {
		Manifest manifest = getManifest(resource);
		if (manifest == null)
			throw new IllegalArgumentException(String.format(
					"Cannot identify symbolic name for resource %s: manifest unavailable", resource.getLocation()));

		String subsystemContent = manifest.getMainAttributes().getValue(SubsystemConstants.SUBSYSTEM_CONTENT);

		if (subsystemContent == null)
			throw new IllegalArgumentException(
					"Not an Subsystem archive: missing 'Subsystem-Content' entry from manifest.");

		Map<String,Map<String,String>> map = OSGiHeader.parseHeader(subsystemContent);

		return map;
	}

	public static String getType(Resource resource) throws IOException {
		Manifest manifest = getManifest(resource);
		if (manifest == null)
			throw new IllegalArgumentException(String.format(
					"Cannot identify version for resource %s: manifest unavailable", resource.getLocation()));
		String type = manifest.getMainAttributes().getValue(SubsystemConstants.SUBSYSTEM_TYPE);
		return type;
	}

	public static Version getVersion(Resource resource) throws IOException {
		Manifest manifest = getManifest(resource);
		if (manifest == null)
			throw new IllegalArgumentException(String.format(
					"Cannot identify version for resource %s: manifest unavailable", resource.getLocation()));
		String versionStr = manifest.getMainAttributes().getValue(SubsystemConstants.SUBSYSTEM_VERSION);
		Version version = (versionStr != null) ? new Version(versionStr) : Version.emptyVersion;
		return version;
	}

}
