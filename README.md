# SmartCity Water Orchestrator

## Università degli Studi di Catania - LM Ingegneria informatica

### Corso di IoT

#### Alessandro Di Stefano - Marco Grassia - Luca Musumeci

# Introduzione
Contrastare le notevoli problematiche in merito all’approvvigionamento dell’acqua è 
diventata regolare incombenza di diversi enti locali nazionali e siciliani.
Si stima che in Sicilia oltre il 50% di acqua si perda nei collegamenti della rete, a fronte di
una media del 35% a livello nazionale, e la causa principale è da cercarsi nelle condutture
idriche obsolete e fatiscenti.

L’assenza di documentazione sulla topologia e/o topografia della rete idrica comporta
enormi difficoltà anche negli interventi d’urgenza necessari a seguito di rotture dei condotti
sotterranei. La problematica si inasprisce ulteriormente in quelle città che negli ultimi anni
hanno subito una forte crescita demografica e che non hanno saputo rispondere alla stessa
con una opportuna riqualificazione delle infrastrutture e dei servizi - nello specifico i servizi
acquedotto - da fornire alla popolazione.

Sono diverse le realtà amministrative in cui un mancato controllo dell’erogazione
comporterebbe difficoltà nell'approvvigionamento quotidiano dell’acqua per i cittadini. Nelle
realtà siciliane, è frequente l’apertura e chiusura periodica, e manuale, del circuito in uscita
verso le diverse zone della città al fine di limitare il deflusso dell’acqua dalle vasche (dovuto
a perdite e/o consumi) e permetterne il rifornimento per il giorno successivo.

Il progetto proposto vuole promuovere una possibile soluzione basata sull’automazione del
sistema idrico e sull’orchestrazione di sensori e attuatori posti nei punti cruciali (quali pozzi,
vasche, e nodi locali nelle diverse zone) della rete al fine di ottimizzare il riempimento dei
serbatoi per garantire qualità del servizio acquedotto. Poiché oltre lo scopo del progetto,
trascuriamo la possibile mancanza di documentazione in merito alla topografia/topologia
della rete idrica di una città, considerandola nota.

# Soluzione proposta
La soluzione proposta si basa sulla tecnologia e sulle metodologie IoT che permettono di
monitorare la quantità d’acqua residua nei serbatoi, le portate in ingresso ed in uscita, e di
gestirne l’approvvigionamento e la erogazione.

È possibile modellare una rete idrica di un servizio acquedotto (A) in due reti:
- R1: costituita da M pozzi ed N vasche;
- R2: rete degli utilizzatori (abitazioni).

Sfruttando l’analogia idraulica-elettrica, per semplificazione e senza perdere di generalità, nel presente progetto rappresentiamo l'intera rete con un solo pozzo tramite un circuito di esempio.

[For more, see Readme (currently, italian only)](https://github.com/eMarco/smart-city-water-orchestrator/raw/master/Relazione%20IoT.pdf)
