package org.jcollectd.agent.api;

public abstract class Packet<T> implements Identifier{
    private Identifier identifier;
    private long interval;

    public Packet(Identifier identifier) {
        if(identifier == null){
            throw new IllegalArgumentException("Identifier should not be null");
        }
        this.identifier = identifier;
    }

    public void setInterval(long interval){
        this.interval = interval;
    }

    public long getInterval(){
        return this.interval;
    }

    @Override
    public long getTime() {
        return identifier.getTime();
    }

    @Override
    public String getHost() {
        return identifier.getHost();
    }

    @Override
    public String getPlugin() {
        return identifier.getPlugin();
    }

    @Override
    public String getPluginInstance() {
        return identifier.getPluginInstance();
    }

    @Override
    public String getType() {
        return identifier.getType();
    }

    @Override
    public String getTypeInstance() {
        return identifier.getTypeInstance();
    }

    @Override
    public String getSource() {
        return identifier.getSource();
    }

    @Override
    public boolean defined(String instance) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    abstract T getData();
}
