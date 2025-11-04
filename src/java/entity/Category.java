
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category implements Serializable{
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private Main_Category main_category;
    
    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private Sub_Category sub_category;

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Main_Category getMain_category() {
        return main_category;
    }

    public void setMain_category(Main_Category main_category) {
        this.main_category = main_category;
    }

    public Sub_Category getSub_category() {
        return sub_category;
    }

    public void setSub_category(Sub_Category sub_category) {
        this.sub_category = sub_category;
    }
}
