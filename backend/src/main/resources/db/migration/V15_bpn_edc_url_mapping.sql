create table bpn_edc_mappings
(
	bpn varchar(255),
	url varchar(255),
	primary key (bpn),
	unique(url)
);

insert into bpn_edc_mappings values('BPNL00000000BJTL', 'https://trace-x-test-edc.dev.demo.catena-x.net/a1');
insert into bpn_edc_mappings values('BPNL00000000BJTK', 'https://trace-x-test-edc.dev.demo.catena-x.net/a2');

insert into bpn_edc_mappings values('BPNL00000003AXS1', 'https://trace-x-test-edc.dev.demo.catena-x.net/b1');
insert into bpn_edc_mappings values('BPNL00000003AXS2', 'https://trace-x-test-edc.dev.demo.catena-x.net/b2');
insert into bpn_edc_mappings values('BPNL00000003AXS3', 'https://trace-x-test-edc.dev.demo.catena-x.net/b3');
