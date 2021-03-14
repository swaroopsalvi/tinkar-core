/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.hl7.tinkar.common.service.CachingService;

@SuppressWarnings("module")
        // 7 in HL7 is not a version reference
module org.hl7.tinkar.common {
    requires java.prefs;

    requires transitive org.hl7.tinkar.roaring;
    requires transitive org.hl7.tinkar.eclipse.collections;
    requires transitive org.hl7.tinkar.activej;
    requires transitive java.logging;

    exports org.hl7.tinkar.common.service;
    exports org.hl7.tinkar.common.id;
    exports org.hl7.tinkar.common.util.text;
    exports org.hl7.tinkar.common.util.time;
    exports org.hl7.tinkar.common.util.uuid;
    exports org.hl7.tinkar.common.binary;

    uses CachingService;
}
