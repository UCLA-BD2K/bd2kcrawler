package org.bd2k.crawler.service;

import org.bd2k.crawler.model.Page;
import org.springframework.stereotype.Service;

@Service("archiveService")
public class PageServiceImpl implements PageService {

	//functionality test
	public String ping() {
		System.out.println((new Page()).ping());
		return "[Archive Service Impl] I am alive";
	}
	
}
