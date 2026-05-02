package tn.utm.kafka;

import java.util.Date;
import java.util.List;

public class Commande {
    private String id;
    private Date date;
    private List<String> articles;
    private double total;

    public Commande() {} // requis par Jackson

    public Commande(String id, Date date, List<String> articles, double total) {
        this.id = id;
        this.date = date;
        this.articles = articles;
        this.total = total;
    }

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getArticles() {
        return articles;
    }

    public void setArticles(List<String> articles) {
        this.articles = articles;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", articles=" + articles +
                ", total=" + total +
                '}';
    }
}