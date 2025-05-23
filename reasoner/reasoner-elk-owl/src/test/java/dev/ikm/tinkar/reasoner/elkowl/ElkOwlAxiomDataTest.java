/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.tinkar.reasoner.elkowl;

import dev.ikm.elk.snomed.owl.SnomedOwlOntology;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElkOwlAxiomDataTest {

	private static final Logger LOG = LoggerFactory.getLogger(ElkOwlAxiomDataTest.class);

	private static ElkOwlData axiomData;

	@BeforeAll
	public static void setup() throws Exception {
		axiomData = new ElkOwlData(SnomedOwlOntology.createOntology().getDataFactory());
	}

	public void getConcept(int id) {
		OWLClass ooc = axiomData.getConcept(id);
		LOG.info("Concept: " + ooc);
		assertEquals(ElkOwlPrefixManager.PREFIX + id, ooc.getIRI().toString());
		assertEquals(id, Integer.parseInt(ooc.getIRI().getShortForm()));
	}

	@Test
	public void getConceptMax() {
		getConcept(Integer.MAX_VALUE);
	}

	@Test
	public void getConceptMin() {
		getConcept(Integer.MIN_VALUE);
	}

	public void getRole(int id) {
		OWLObjectProperty oop = axiomData.getRole(id);
		LOG.info("Role: " + oop);
		assertEquals(ElkOwlPrefixManager.PREFIX + id, oop.getIRI().toString());
		assertEquals(id, Integer.parseInt(oop.getIRI().getShortForm()));
	}

	@Test
	public void getRoleMax() {
		getRole(Integer.MAX_VALUE);
	}

	@Test
	public void getRoleMin() {
		getRole(Integer.MIN_VALUE);
	}

}
