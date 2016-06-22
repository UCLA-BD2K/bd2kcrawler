package org.bd2kccc.crawler.service;

import org.bd2kccc.crawler.model.ArchivedPage;
import org.springframework.stereotype.Service;

@Service("archiveService")
public class ArchiveServiceImpl implements ArchiveService {

	
	public String ping() {
		System.out.println((new ArchivedPage()).ping());
		return "[Archive Service Impl] I am alive";
	}
}
