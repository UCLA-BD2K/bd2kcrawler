package org.bd2k.crawler.service;

import org.bd2k.crawler.model.Publication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service("publicationService")
public class PublicationServiceImpl implements PublicationService {

	// may need refactoring
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		
	@Override
	public Publication getPublicationByID(String id) {
		
		Query q = new Query(Criteria.where("id").is(id));
		Publication p = mongoOperation.findOne(q, Publication.class);
		
		return p;
	}

	@Override
	public Publication getPublicationByPmid(String pmid) {
		
		Query q = new Query(Criteria.where("pmid").is(pmid));
		Publication p = mongoOperation.findOne(q, Publication.class);
		
		return p;
	}

	@Override
	public void savePublication(Publication p) {
	
		mongoOperation.save(p);	
	}

	@Override
	public void saveOrUpdatePublication(Publication p) {
		
		Publication check = getPublicationByPmid(p.getPmid());
		
		// if there is already a publication stored
		if(check != null) {
			
			check.setAuthors(p.getAuthors());
			check.setCenters(p.getCenters());
			check.setJournal(p.getJournal());
			check.setTitle(p.getTitle());
			check.setPubDate(p.getPubDate());
			check.setPmid(p.getPmid());
			check.setLastCrawlTime(p.getLastCrawlTime());
			
			mongoOperation.save(check);
		}
		else {
			
			// need to store a new document
			mongoOperation.save(p);
		}
		
	}

}
