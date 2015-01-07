/**
 * 
 */
package org.osgi.service.indexer.impl;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;
import org.osgi.service.indexer.*;
import org.osgi.service.subsystem.*;

public class TestSubsystemAnalyzer extends TestCase {

	public void testContentAndIdentity() throws Exception {
		SubsystemAnalyzer analyzer = new SubsystemAnalyzer(new NullLogSvc());
		LinkedList<Capability> capabilities = new LinkedList<Capability>();

		Resource resource = new EsaResource(new File("testdata/subsystem-feature-decl.esa"));

		analyzer.analyzeResource(resource, capabilities, Collections.EMPTY_LIST);

		assertEquals(2, capabilities.size());

		Capability osgiIdCapability = capabilities.get(0);
		assertEquals("osgi.identity", osgiIdCapability.getNamespace());
		assertEquals("http-subsystem", osgiIdCapability
				.getAttributes().get("osgi.identity"));
		assertEquals(SubsystemConstants.SUBSYSTEM_TYPE_FEATURE, osgiIdCapability.getAttributes().get("type"));
		assertEquals(new Version("1.0.0"), osgiIdCapability
				.getAttributes().get("version"));

		Capability osgiContentCapability = capabilities.get(1);
		assertEquals("osgi.content", osgiContentCapability.getNamespace());
		assertEquals("7726e3330fe25261dce6bf32e4680c4bfdcd1e59b8982892a6f1e957a4cb8536", osgiContentCapability
				.getAttributes().get("osgi.content"));
		assertEquals("testdata/subsystem-feature-decl.esa", osgiContentCapability.getAttributes().get("url"));
		assertEquals(2103l, osgiContentCapability.getAttributes().get("size"));
		assertEquals("application/vnd.osgi.subsystem", osgiContentCapability.getAttributes().get("mime"));
	}

}
