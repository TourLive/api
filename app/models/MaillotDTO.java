package models;

public class MaillotDTO {
    private Long id;
    private String type;
    private String name;
    private String color;
    private String partner;
    private long riderId;

    public MaillotDTO(Maillot maillot) {
        this.id = maillot.getId();
        this.type = maillot.getType();
        this.name = maillot.getName();
        this.color = maillot.getColor();
        this.partner = maillot.getPartner();
        if (maillot.getRiderStageConnections().isEmpty()) {
            this.riderId = 0;
        } else {
            this.riderId = maillot.getRiderStageConnections().get(0).getRider().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public long getRiderId() {
        return riderId;
    }

    public void setRiderId(long riderId) {
        this.riderId = riderId;
    }
}
