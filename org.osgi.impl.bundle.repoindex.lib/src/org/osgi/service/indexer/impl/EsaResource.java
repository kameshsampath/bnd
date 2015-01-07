package org.osgi.service.indexer.impl;

import java.io.*;
import java.util.jar.*;

import org.osgi.service.indexer.*;

public class EsaResource extends JarResource {

	private Manifest	manifest;

	public EsaResource(File file) throws IOException {
		super(file);
	}

	public Manifest getManifest() throws IOException {
		synchronized (this) {
			if (manifest == null) {
				Resource manifestResource = getChild("OSGI-INF/SUBSYSTEM.MF");
				if (manifestResource != null) {
					try {
						manifest = new Manifest(manifestResource.getStream());
					}
					finally {
						manifestResource.close();
					}
				}
			}
			return manifest;
		}
	}

}
