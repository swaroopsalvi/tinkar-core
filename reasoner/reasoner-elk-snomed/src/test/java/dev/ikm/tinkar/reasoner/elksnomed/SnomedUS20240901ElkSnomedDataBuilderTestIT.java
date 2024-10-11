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
package dev.ikm.tinkar.reasoner.elksnomed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.ikm.elk.snomed.SnomedOntology;
import dev.ikm.elk.snomed.model.Concept;
import dev.ikm.elk.snomed.owl.OwlTransformer;
import dev.ikm.elk.snomed.owl.SnomedOwlOntology;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.util.uuid.UuidUtil;

public class SnomedUS20240901ElkSnomedDataBuilderTestIT extends ElkSnomedDataBuilderTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SnomedUS20240901ElkSnomedDataBuilderTestIT.class);

	static {
		stated_count = 393479;
		active_count = 370291;
		inactive_count = 23188;
		test_case = "snomed-us-20240901";
	}

	// TODO get all this back once test db are available

	public static final String db = "Snomedct-us-20240901-spinedarray-data-20240920";

	protected String getDir() {
		// TODO
//		return "target/data/snomed-test-data-" + getEditionDir() + "-" + getVersion();
		return "target/db/snomed-test-data-" + getEditionDir() + "-" + getVersion();
	}

	protected String getEdition() {
		return "US1000124";
	}

	protected String getEditionDir() {
		return "us";
	}

	protected String getVersion() {
		return "20240901";
	}

	protected Path axioms_file = Paths.get(getDir(),
			"sct2_sRefset_OWLExpressionSnapshot_" + getEdition() + "_" + getVersion() + ".txt");

	protected Path rels_file = Paths.get(getDir(),
			"sct2_Relationship_Snapshot_" + getEdition() + "_" + getVersion() + ".txt");

	@BeforeAll
	public static void startPrimitiveData() throws IOException {
		setupPrimitiveData(db);
		PrimitiveData.start();
	}

	@Test
	public void compareDefs() throws Exception {
		assumeTrue(Files.exists(axioms_file), "No file: " + axioms_file);
		assumeTrue(Files.exists(rels_file), "No file: " + rels_file);
		LOG.info("Files exist");
		LOG.info("\t" + axioms_file);
		LOG.info("\t" + rels_file);
		ElkSnomedData data = buildSnomedData();
		int missing_concept_cnt = 0;
//		int missing_role_cnt = 0;
		SnomedOwlOntology ontology = SnomedOwlOntology.createOntology();
		ontology.loadOntology(axioms_file);
		SnomedOntology snomedOntology = new OwlTransformer().transform(ontology);
		for (Concept con : snomedOntology.getConcepts()) {
			UUID uuid = UuidUtil.fromSNOMED("" + con.getId());
			int nid = PrimitiveData.nid(uuid);
			Concept data_con = data.getConcept(nid);
			if (data_con == null) {
				LOG.error("No concept: " + con);
				missing_concept_cnt++;
				continue;
			}
			if (con.getDefinitions().size() != data_con.getDefinitions().size())
				LOG.error("Defs: " + con + " " + con.getDefinitions().size() + " " + data_con.getDefinitions().size()
						+ " " + nid + " " + uuid);

			if (con.getGciDefinitions().size() != data_con.getGciDefinitions().size())
				LOG.error("Gcis: " + con);
		}
		// TODO 138875005 |SNOMED CT Concept (SNOMED RT+CTV3)|
		// TODO 734146004 |OWL ontology namespace (OWL metadata concept)|
		assertEquals(0, missing_concept_cnt);
//		for (OWLObjectProperty clazz : ontology.getOwlObjectProperties()) {
//			long id = SnomedOwlOntology.getId(clazz);
//			UUID uuid = UuidUtil.fromSNOMED("" + id);
//			int nid = PrimitiveData.nid(uuid);
//			if (axiom_data.nidRoleMap.get(nid) == null) {
////				LOG.info("No role: " + clazz);
//				missing_role_cnt++;
//			}
//			axiom_data_role_nids.remove(nid);
//			// All the roles are being added as concepts, so let's remove them from the
//			// concept nids
//			axiom_data_concept_nids.remove(nid);
//		}
//		// TODO 609096000 |Role group (attribute)|
//		assertEquals(0, missing_role_cnt);
////		axiom_data_concept_nids.forEach(x -> {
////			LOG.info("Extra concept: " + x + " " + PrimitiveData.text(x));
////			LOG.info("\t" + PrimitiveData.publicId(x));
////			LOG.info("\t" + axiom_data.nidAxiomsMap.get(x));
////		});
//		assertEquals(294, axiom_data_concept_nids.size());
////		axiom_data_role_nids.forEach(x -> {
////			LOG.info("Extra role: " + x + " " + PrimitiveData.text(x));
////			LOG.info("\t" + axiom_data.nidAxiomsMap.get(x));
////		});
//		assertEquals(11, axiom_data_role_nids.size());
	}

}