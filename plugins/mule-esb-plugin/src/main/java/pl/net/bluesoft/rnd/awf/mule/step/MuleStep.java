package pl.net.bluesoft.rnd.awf.mule.step;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import org.hibernate.collection.PersistentSet;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import pl.net.bluesoft.rnd.awf.mule.MulePluginManager;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstance;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstanceAttribute;
import pl.net.bluesoft.rnd.processtool.model.ProcessInstanceSimpleAttribute;
import pl.net.bluesoft.rnd.processtool.steps.ProcessToolProcessStep;
import pl.net.bluesoft.rnd.processtool.ui.widgets.annotations.AliasName;
import pl.net.bluesoft.rnd.processtool.ui.widgets.annotations.AutoWiredProperty;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tlipski@bluesoft.net.pl
 */
@AliasName(name = "MuleStep")
public class MuleStep implements ProcessToolProcessStep {

    @AutoWiredProperty
    private String destinationEndpointUrl;

    private Object payload;

    @AutoWiredProperty
    private boolean asynchronous = false;

    @AutoWiredProperty
    private long timeout = -1;

    private MulePluginManager mulePluginManager;

    public MuleStep(MulePluginManager mulePluginManager) {
        this.mulePluginManager = mulePluginManager;
    }

    @Override
    public String invoke(ProcessInstance processInstance, Map params) throws Exception {
        try {
            payload = params.get("payload");
            LocalMuleClient client = mulePluginManager.getMuleContext().getClient();
//            XStream xs = new XStream();
//            xs.registerConverter(new MyPersistentSetConverter(xs.getMapper()), XStream.PRIORITY_VERY_HIGH);
//            xs.omitField(ProcessInstance.class, "definition");
//            xs.omitField(ProcessInstance.class, "processLogs");
//            String input = xs.toXML(processInstance);
            if (asynchronous) {
                client.dispatch(destinationEndpointUrl, processInstance, null);
            } else {
                MuleMessage muleMessage = client.send(destinationEndpointUrl,
                                                      payload != null ? payload : processInstance,
                                                      null, timeout);
                if (muleMessage != null) {
                    Object payload = muleMessage.getPayload();
                    if (payload instanceof String) {
                        return (String)payload;
                    } else if (payload instanceof ProcessInstanceAttribute) {
                        ProcessInstanceAttribute pia = (ProcessInstanceAttribute) payload;
                        ProcessInstanceAttribute attributeByKey = processInstance.findAttributeByKey(pia.getKey());
                        if (attributeByKey != null) {
                            processInstance.removeAttribute(attributeByKey);
                        }
                        processInstance.addAttribute(pia);
                        return pia.toString();
                    } else if (payload instanceof ProcessInstanceAttribute[]) {
                        ProcessInstanceAttribute[] pias = (ProcessInstanceAttribute[]) payload;
                        for (ProcessInstanceAttribute pia : pias) {
                            ProcessInstanceAttribute attributeByKey = processInstance.findAttributeByKey(pia.getKey());
                            if (attributeByKey != null) {
                                processInstance.removeAttribute(attributeByKey);
                            }
                            processInstance.addAttribute(pia);
                        }
                    } else if (payload instanceof Iterable) {
                        Iterable pias = (Iterable) payload;
                        for (Object o : pias) {
                            if (o instanceof ProcessInstanceAttribute) {
                                ProcessInstanceAttribute pia = (ProcessInstanceAttribute) o;
                                ProcessInstanceAttribute attributeByKey = processInstance.findAttributeByKey(pia.getKey());
                                if (attributeByKey != null) {
                                    processInstance.removeAttribute(attributeByKey);
                                }
                                processInstance.addAttribute(pia);
                            }
                        }
                    }

                }
            }
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }

    }

    public String getDestinationEndpointUrl() {
        return destinationEndpointUrl;
    }

    public void setDestinationEndpointUrl(String destinationEndpointUrl) {
        this.destinationEndpointUrl = destinationEndpointUrl;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    //     private static class MyPersistentSetConverter extends CollectionConverter {
//        public MyPersistentSetConverter(Mapper mapper) {
//            super(mapper);
//        }
//
//        @Override
//        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
//            Set ps = (Set) source;
//            super.marshal(new HashSet(ps), writer, context);
//        }
//
//        @Override
//        public boolean canConvert(Class type) {
//            return type.isAssignableFrom(PersistentSet.class);
//        }
//    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
