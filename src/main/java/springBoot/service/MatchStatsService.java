package springBoot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springBoot.entity.MatchStats;
import springBoot.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/*
    @Service:
    We mark beans with @Service to indicate that it's holding the business logic.
    So there's no any other specialty except using it in the service layer
*/
@Service
@Transactional
public class MatchStatsService {


    /*
        @Autowired Annotations:
        This annotation allows Spring to resolve and inject collaborating
        beans into your bean.
        Once annotation injection is enabled, autowiring can be used on
        properties, setters, and constructors.

        The annotation can be used directly on properties, therefor eliminating
        the need for getters and setters.

        The @Autowired annotation can be used on setter methods. when the annotation
        is used on a setter method, the setter method is called with the instance of
        the setter, when the main class is created.

        The @Autowired annotation can also be used on constructors. In the below example,
         when the annotation is used on a constructor, an instance of FooFormatter is injected
         as an argument to the constructor when FooService is created:

public class FooService {

    private FooFormatter fooFormatter;

    @Autowired
    public FooService(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
    */
    @Autowired
    private EntityManager em;

    public MatchStats getMatchStats(String username){

        TypedQuery<MatchStats> query = em.createQuery(
                "select m from MatchStats m where m.user.username=?1", MatchStats.class);
        query.setParameter(1,username);

        List<MatchStats> results = query.getResultList();
        if(!results.isEmpty()){
            return results.get(0);
        }

        User user = em.find(User.class, username);
        if(user == null){
            throw new IllegalArgumentException("No existing user: " + username);
        }

        MatchStats match = new MatchStats();
        match.setUser(user);
        em.persist(match);

        return match;
    }

    public void reportVictory(String username){

        MatchStats match = getMatchStats(username);

        match.setVictories( 1 + match.getVictories());
    }
    public void reportDefeat(String username){

        MatchStats match = getMatchStats(username);

        match.setVictories( 1 + match.getDefeats());
    }
}
