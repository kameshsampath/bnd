package org.osgi.service.indexer.impl;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.jar.*;

import org.osgi.framework.*;
import org.osgi.service.indexer.*;
import org.osgi.service.indexer.impl.types.*;
import org.osgi.service.indexer.impl.util.*;
import org.osgi.service.log.*;

public class SubsystemAnalyzer implements ResourceAnalyzer {

	private static final String	SHA_256	= "SHA-256";

	// Filename suffix for Subsystem bundle files
	@SuppressWarnings("unused")
	private static final String	SUFFIX_ESA = ".esa";

	private final ThreadLocal<GeneratorState> state	= new ThreadLocal<GeneratorState>();

	@SuppressWarnings("unused")
	private LogService log;

	public SubsystemAnalyzer(LogService log) {
		this.log = log;
	}

	@Override
	public void analyzeResource(Resource resource, List<Capability> capabilities, List<Requirement> requirements)
			throws Exception {

		doSubsystemIdentity(resource, capabilities);
		doContent(resource, MimeType.Subsystem, capabilities);
		// TODO handle Embedded or inline subsystems
		doSubsystemContent(resource, capabilities);

	}

	void setStateLocal(GeneratorState state) {
		this.state.set(state);
	}

	private GeneratorState getStateLocal() {
		return state.get();
	}

	private void doSubsystemContent(Resource resource, List<Capability> capabilities) throws IOException {
		Manifest manifest = SubsystemUtil.getManifest(resource);
		// WIP
	}
	private void doSubsystemIdentity(Resource resource, List<Capability> capabilities)
			throws IOException {
		Manifest manifest = SubsystemUtil.getManifest(resource);
		if (manifest == null) {
			throw new IllegalArgumentException("Missing subsystem manifest.");
		}

		String type = SubsystemUtil.getType(resource);

		SymbolicName subsystemSymbolicName = SubsystemUtil.getSymbolicName(resource);

		Version version = SubsystemUtil.getVersion(resource);

		Builder builder = new Builder().setNamespace(Namespaces.NS_IDENTITY)
				.addAttribute(Namespaces.NS_IDENTITY, subsystemSymbolicName.getName())
				.addAttribute(Namespaces.ATTR_IDENTITY_TYPE, type)
				.addAttribute(Namespaces.ATTR_VERSION, version);

		capabilities.add(builder.buildCapability());

	}

	private void doContent(Resource resource, MimeType mimeType, List< ? super Capability> capabilities)
			throws Exception {
		Builder builder = new Builder().setNamespace(Namespaces.NS_CONTENT);

		String sha = calculateSHA(resource);
		builder.addAttribute(Namespaces.NS_CONTENT, sha);

		String location = calculateLocation(resource);
		builder.addAttribute(Namespaces.ATTR_CONTENT_URL, location);

		long size = resource.getSize();
		if (size > 0L)
			builder.addAttribute(Namespaces.ATTR_CONTENT_SIZE, size);

		builder.addAttribute(Namespaces.ATTR_CONTENT_MIME, mimeType.toString());

		capabilities.add(builder.buildCapability());
	}

	private String calculateSHA(Resource resource) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(SHA_256);
		byte[] buf = new byte[1024];

		InputStream stream = null;
		try {
			stream = resource.getStream();
			while (true) {
				int bytesRead = stream.read(buf, 0, 1024);
				if (bytesRead < 0)
					break;

				digest.update(buf, 0, bytesRead);
			}
		}
		finally {
			if (stream != null)
				stream.close();
		}

		return Hex.toHexString(digest.digest());
	}

	private String calculateLocation(Resource resource) throws IOException {
		String location = resource.getLocation();

		File path = new File(location);
		String fileName = path.getName();
		String dir = path.getAbsoluteFile().getParentFile().toURI().toURL().toString();

		String result = location;

		GeneratorState state = getStateLocal();
		if (state != null) {
			String rootUrl = state.getRootUrl().toString();
			if (!rootUrl.endsWith("/"))
				rootUrl += "/";

			if (rootUrl != null) {
				if (dir.startsWith(rootUrl))
					dir = dir.substring(rootUrl.length());
				else
					throw new IllegalArgumentException("Cannot index files above the root URL.");
			}

			String urlTemplate = state.getUrlTemplate();
			if (urlTemplate != null) {
				String subsystemSymbolicName = (urlTemplate.indexOf("%s") == -1) ? "" : SubsystemUtil.getSymbolicName(resource).getName();
				Version version = (urlTemplate.indexOf("%v") == -1) ? Version.emptyVersion : SubsystemUtil
						.getVersion(resource);
				urlTemplate = urlTemplate.replaceAll("%s", "%1\\$s").replaceAll("%f", "%2\\$s")
						.replaceAll("%p", "%3\\$s").replaceAll("%v", "%4\\$s");
				result = String.format(urlTemplate, subsystemSymbolicName, fileName, dir, version);
			} else {
				result = dir + fileName;
			}
		}

		return result;
	}

}
