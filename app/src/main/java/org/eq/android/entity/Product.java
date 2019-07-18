package org.eq.android.entity;

/**
 * 商品券
 */
public class Product {
    private int id; //商品id
    private String productName; //商品名称
    private String name; //个别接口
    private int unitPrice; //单价
    private String img; //图片地址
    private String desc; //商品描述
    private String receive; //提货说明
    private String expirationStart; //有效期
    private String expirationEnd; //过期时间

    private int number; //持有量
    private String acceptName; //承兑商名称
    private String acceptImg; //承兑商图片
    private String acceptAddress; //承兑商简介
    private String acceptIntro; //承兑商简介
    private String issuerName; //发行商名称
    private String issuerImg; //发行商图片
    private String issuerAddress; //发行商地址
    private String issuerIntro; //发行商简介
    private String productImg;

    private int lockedNum; //锁定量

    public Product() {
    }

    public Product(int id, String productName, String name, int unitPrice, String img, String desc, String receive, String expirationStart, String expirationEnd, int number, String acceptName, String acceptImg, String acceptAddress, String acceptIntro, String issuerName, String issuerImg, String issuerAddress, String issuerIntro, String productImg, int lockedNum) {
        this.id = id;
        this.productName = productName;
        this.name = name;
        this.unitPrice = unitPrice;
        this.img = img;
        this.desc = desc;
        this.receive = receive;
        this.expirationStart = expirationStart;
        this.expirationEnd = expirationEnd;
        this.number = number;
        this.acceptName = acceptName;
        this.acceptImg = acceptImg;
        this.acceptAddress = acceptAddress;
        this.acceptIntro = acceptIntro;
        this.issuerName = issuerName;
        this.issuerImg = issuerImg;
        this.issuerAddress = issuerAddress;
        this.issuerIntro = issuerIntro;
        this.productImg = productImg;
        this.lockedNum = lockedNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getExpirationStart() {
        return expirationStart;
    }

    public void setExpirationStart(String expirationStart) {
        this.expirationStart = expirationStart;
    }

    public String getExpirationEnd() {
        return expirationEnd;
    }

    public void setExpirationEnd(String expirationEnd) {
        this.expirationEnd = expirationEnd;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getAcceptImg() {
        return acceptImg;
    }

    public void setAcceptImg(String acceptImg) {
        this.acceptImg = acceptImg;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getAcceptIntro() {
        return acceptIntro;
    }

    public void setAcceptIntro(String acceptIntro) {
        this.acceptIntro = acceptIntro;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getIssuerImg() {
        return issuerImg;
    }

    public void setIssuerImg(String issuerImg) {
        this.issuerImg = issuerImg;
    }

    public String getIssuerAddress() {
        return issuerAddress;
    }

    public void setIssuerAddress(String issuerAddress) {
        this.issuerAddress = issuerAddress;
    }

    public String getIssuerIntro() {
        return issuerIntro;
    }

    public void setIssuerIntro(String issuerIntro) {
        this.issuerIntro = issuerIntro;
    }

    public int getLockedNum() {
        return lockedNum;
    }

    public void setLockedNum(int lockedNum) {
        this.lockedNum = lockedNum;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
