package org.jcollectd.agent.api;

public abstract class Packet<T> {
    private Identifier identifier;
    private long interval;

    public Packet(Identifier identifier) {
        this.identifier = identifier;
    }

    abstract T getData();

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }


    public void setInterval(long interval){
        this.interval = interval;
    }

    public long getInterval(){
        return this.interval;
    }
}
