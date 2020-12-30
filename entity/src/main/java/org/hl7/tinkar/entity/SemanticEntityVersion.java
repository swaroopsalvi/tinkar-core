package org.hl7.tinkar.entity;

import io.activej.bytebuf.ByteBuf;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.hl7.tinkar.component.Component;
import org.hl7.tinkar.component.DefinitionForSemantic;
import org.hl7.tinkar.component.SemanticVersion;
import org.hl7.tinkar.dto.FieldDataType;
import org.hl7.tinkar.entity.internal.Get;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SemanticEntityVersion
        extends EntityVersion
        implements SemanticVersion {

    protected final MutableList<Object> fields = Lists.mutable.empty();

    @Override
    public FieldDataType dataType() {
        return FieldDataType.SEMANTIC_VERSION;
    }

    private SemanticEntity getSemanticEntity() {
        return (SemanticEntity) this.chronology;
    }

    @Override
    protected void finishVersionFill(ByteBuf readBuf) {
        fields.clear();
        int fieldCount = readBuf.readInt();
        for (int i = 0; i < fieldCount; i++) {
            FieldDataType dataType = FieldDataType.fromToken(readBuf.readByte());
            switch (dataType) {
                case BOOLEAN: {
                    fields.add(readBuf.readBoolean());
                    break;
                }
                case FLOAT:{
                    fields.add(readBuf.readFloat());
                    break;
                }
                case BYTE_ARRAY: {
                    int length = readBuf.readInt();
                    byte[] bytes = new byte[length];
                    readBuf.read(bytes);
                    fields.add(bytes);
                    break;
                }
                case INTEGER: {
                    fields.add(readBuf.readInt());
                    break;
                }
                case STRING: {
                    int length = readBuf.readInt();
                    byte[] bytes = new byte[length];
                    readBuf.read(bytes);
                    fields.add(new String(bytes, UTF_8));
                    break;
                }

                case IDENTIFIED_THING:
                case INSTANT:
                case DIGRAPH:
                default:
                    throw new UnsupportedOperationException("Can't handle field read of type: " +
                            dataType);
            }
        }
    }

    @Override
    protected int subclassFieldBytesSize() {
        int size = 0;
        for (Object field: fields) {
            if (field instanceof Boolean bol) {
                size += 2;
            } else if (field instanceof Float flt) {
                size += 5;
            } else if (field instanceof byte[] byteArray) {
                size += (5 + byteArray.length);
            } else if (field instanceof Integer integer) {
                size += 5;
            } else if (field instanceof String string) {
                size += (5 + (string.length() * 2)); // token, length, upper bound on string bytes (average < 16 bit chars for UTF8...).
            } else if (field instanceof Entity entity) {
                size += 5;
            } else if (field instanceof EntityProxy proxy) {
                size += 5;
            } else if (field instanceof Component component) {
                size += 5;
            } else {
                throw new UnsupportedOperationException("Can't handle field size for type: " +
                        field.getClass().getName());
            }        }
        return size;
    }

    @Override
    protected void writeVersionFields(ByteBuf writeBuf) {
        writeBuf.writeInt(fields.size());
        for (Object field: fields) {
            if (field instanceof Boolean bol) {
                writeBuf.writeByte(FieldDataType.BOOLEAN.token);
                writeBuf.writeBoolean(bol);
            } else if (field instanceof Float flt) {
                writeBuf.writeByte(FieldDataType.FLOAT.token);
                writeBuf.writeFloat(flt);
            } else if (field instanceof byte[] byteArray) {
                writeBuf.writeByte(FieldDataType.BYTE_ARRAY.token);
                writeBuf.writeInt(byteArray.length);
                writeBuf.write(byteArray);
            } else if (field instanceof Integer integer) {
                writeBuf.writeByte(FieldDataType.INTEGER.token);
                writeBuf.writeInt(integer);
            } else if (field instanceof String string) {
                writeBuf.writeByte(FieldDataType.STRING.token);
                byte[] bytes = string.getBytes(UTF_8);
                writeBuf.writeInt(bytes.length);
                writeBuf.write(bytes);
            } else if (field instanceof Entity entity) {
                writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
                writeBuf.writeInt(entity.nid);
            } else if (field instanceof EntityProxy proxy) {
                writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
                writeBuf.writeInt(proxy.nid);
            } else if (field instanceof Component component) {
                writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
                writeBuf.writeInt(Get.entityService().nidForUuids(component));
            } else {
                throw new UnsupportedOperationException("Can't handle field write of type: " +
                        field.getClass().getName());
            }
        }
    }

    @Override
    public Component referencedComponent() {
        return Get.entityService().getEntityFast(getSemanticEntity().referencedComponentNid);
    }

    @Override
    public DefinitionForSemantic definitionForSemantic() {
        return Get.entityService().getEntityFast(getSemanticEntity().setNid);
    }

    @Override
    public ImmutableList<Object> fields() {
        return fields.toImmutable();
    }

    public static SemanticEntityVersion make(SemanticEntity semanticEntity, ByteBuf readBuf) {
        SemanticEntityVersion version = new SemanticEntityVersion();
        version.fill(semanticEntity, readBuf);
        return version;
    }

    public static SemanticEntityVersion make(SemanticEntity semanticEntity, SemanticVersion versionToCopy) {
        SemanticEntityVersion version = new SemanticEntityVersion();
        version.fill(semanticEntity, versionToCopy);
        version.fields.clear();
        for (Object obj: versionToCopy.fields()) {
            if (obj instanceof Boolean bol) {
               version.fields.add(bol);
            } else if (obj instanceof Float flt) {
                version.fields.add(flt);
            } else if (obj instanceof byte[] byteArray) {
                version.fields.add(byteArray);
            } else if (obj instanceof Integer integer) {
                version.fields.add(integer);
            } else if (obj instanceof String string) {
                version.fields.add(string);
            } else if (obj instanceof Component component) {
                version.fields.add(EntityProxy.make(Get.entityService().nidForUuids(component)));
            } else {
                throw new UnsupportedOperationException("Can't handle field conversion of type: " +
                        obj.getClass().getName());
            }
        }
        return version;
    }
}
