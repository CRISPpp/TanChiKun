package cn.cirsp.entity;

public class Block {
    private boolean isHead;
    private boolean isBody;
    private boolean isBasketball;
    private int id;

    public Block() {
        isBasketball = false;
        isBody = false;
        isHead = false;
        id = -1;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public void setBody(boolean body) {
        isBody = body;
    }

    public void setBasketball(boolean basketball) {
        isBasketball = basketball;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHead() {
        return isHead;
    }

    public boolean isBody() {
        return isBody;
    }

    public boolean isBasketball() {
        return isBasketball;
    }
}
