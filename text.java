

private void startStatisticsCollection() {
	portStatsCollector = threadPoolService
	.getScheduledExecutor()
	.scheduleAtFixedRate(new FlowStatsCollector(), portStatsInterval, portStatsInterval, TimeUnit.SECONDS);
	
}

private class FlowStatsCollector implements Runnable {
	public void run() {
		Map<DatapathId, List<OFStatsReply>> replies = getSwitchStatistics(switchService.getAllSwitchDpids(), OFStatsType.FLOW )
		for(Entry<DatapathId, List<OFStatsReply>> e : replies.entrySet()) {
			for (OFStatsReply r : e.getValue()) {
				OFFlowStatsReply aggRep = (OFAggregateStatsReply) r;
				for (OFFlowStatsEntry aggEntry : aggRep.getEntries()) {
					U64 byteCount = aggEntry.getByteCount();
					U64 packetCount = aggEntry.getPacketCount();
					System.out.println("Byte Count = " + byteCount.toString());
					System.out.println("Packet Count = " + packetCount.toString());
				}
			}
		}
	}
}
