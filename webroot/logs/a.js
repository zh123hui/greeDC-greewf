private static void loadConfigInfo(Context ctx) {
/* 446 */     if (ejbUtilsCache.containsKey(ctx.getAIS())) {
/* 447 */       return;
/*     */     }
/*     */ 
/* 450 */     if (logger.isInfoEnabled()) {
/* 451 */       logger.info("EJBFactory get AIS config from DeployInfoManager");
/*     */     }
/*     */ 
/* 454 */     SolutionDeployInfo slnDeployInfo = DeployInfoManagerFactory.getDeployInfoManager().getSolutionDeployInfo(ctx.getSolution());
/*     */ 
/* 459 */     AISDeployInfo aisDeployInfo = null;
/* 460 */     if (slnDeployInfo != null) {
/* 461 */       aisDeployInfo = slnDeployInfo.getAISInfo(ctx.getAIS());
/*     */     }
/* 463 */     EJBUtils[] ejbUtils = null;
/* 464 */     if (aisDeployInfo == null) {
/* 465 */       ejbUtils = new EJBUtils[1];
/* 466 */       ejbUtils[0] = new EJBUtils("jdbc/" + ctx.getAIS());
/* 467 */       ejbUtilsCache.put(ctx.getAIS(), ejbUtils);
/*     */ 
/* 470 */       IEJBUtils queryDS = new EJBUtils("jdbc/" + ctx.getAIS() + "_Query");
/*     */ 
/* 472 */       if (isQueryDataSourceAvaliable(queryDS)) {
/* 473 */         queryDSCache.put(ctx.getAIS(), queryDS);
/*     */       }
/*     */       else {
/* 476 */         queryDSCache.put(ctx.getAIS(), ejbUtils[0]);
/*     */       }
/*     */ 
/* 479 */       aisCache.put(ctx.getAIS(), Boolean.FALSE);
/*     */     } else {
/* 481 */       AppServerDeployInfo[] appServerInfos = aisDeployInfo.getServerInfos();
/*     */ 
/* 483 */       int len = appServerInfos.length;
/* 484 */       ejbUtils = new EJBUtils[len];
/* 485 */       for (int i = 0; i < len; ++i) {
/* 486 */         String contextFactory = appServerInfos[i].getContextFactory();
/*     */ 
/* 488 */         String jndiURL = appServerInfos[i].getJndiURL();
/* 489 */         String securityCredential = appServerInfos[i].getSecurityCredential();
/*     */ 
/* 491 */         String securityPrincipal = appServerInfos[i].getSecurityPrincipal();
/*     */ 
/* 494 */         String datasource = appServerInfos[i].getDatasource();
/*     */ 
/* 496 */         if (datasource == null) {
/* 497 */           datasource = "jdbc/" + ctx.getAIS();
/*     */         }
/* 499 */         ejbUtils[i] = new EJBUtils(datasource, jndiURL, contextFactory, securityCredential, securityPrincipal);
/*     */ 
/* 507 */         if (i == 0) {
/* 508 */           if (jndiURL == null)
/* 509 */             aisCache.put(ctx.getAIS(), new Boolean(false));
/*     */           else {
/* 511 */             aisCache.put(ctx.getAIS(), new Boolean(true));
/*     */           }
/*     */ 
/* 515 */           IEJBUtils queryDS = new EJBUtils(datasource + "_Query", jndiURL, contextFactory, securityCredential, securityPrincipal);
/*     */ 
/* 522 */           if (isQueryDataSourceAvaliable(queryDS)) {
/* 523 */             queryDSCache.put(ctx.getAIS(), queryDS);
/*     */           }
/*     */           else {
/* 526 */             queryDSCache.put(ctx.getAIS(), ejbUtils[0]);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 531 */       ejbUtilsCache.put(ctx.getAIS(), ejbUtils);
/*     */     }
/*     */   }