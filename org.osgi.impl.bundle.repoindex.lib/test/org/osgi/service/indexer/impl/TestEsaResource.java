package org.osgi.service.indexer.impl;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.indexer.*;
import org.osgi.service.indexer.impl.types.*;

public class TestEsaResource extends TestCase {

	public void testSubsystemSymbolicName() throws Exception {
		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));
		SymbolicName subsystemSymbolicName = SubsystemUtil.getSymbolicName(resource);
		assertEquals("http-subsystem",
				subsystemSymbolicName.getName());
	}

	public void testSubsystemVersion() throws Exception {
		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));
		Version version = SubsystemUtil.getVersion(resource);
		assertEquals("1.0.0", version.toString());
	}

	public void testSubsystemType() throws Exception {
		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));
		String type = SubsystemUtil.getType(resource);
		assertEquals("osgi.subsystem.feature", type);
	}

	public void testSubsystemMimeType() throws Exception {
		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));
		MimeType mimeType = SubsystemUtil.getMimeType(resource);
		assertEquals("application/vnd.osgi.subsystem", mimeType.toString());
	}

	public void testSubsystemContent() throws Exception {
		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));
		Map<String,Map<String,String>> map = SubsystemUtil.getSubsystemContent(resource);
		assertFalse(map.isEmpty());
		System.out.println(map);
	}
}
