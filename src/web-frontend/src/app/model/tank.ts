import { GenericValue } from './generic-value';
import { Trigger } from './trigger';
import { Valve } from './valve';

export class Tank extends GenericValue {
  capacity: number;

  inputFlowRate: number;
  outputFlowRate: number;

  tankId: number;

  trigger: Trigger;

  valve: Valve;


  static labels: string[] = ["Timestamp", "Capacity", "Input Flow Rate", "Output Flow Rate", "ID"];
  static y_label: string = "MB";
  static toArray(stat : Tank): any {
    return [new Date(stat.id.timestamp*1000), stat.capacity, stat.inputFlowRate, stat.outputFlowRate, stat.tankId];
  }
}
/*
{"className":"org.unict.ing.iot.utils.model.Tank",
"capacity":372.7488,
"inputFlowRate":3.0681,
"outputFlowRate":3.7275E-7,
"tankId":1,
"trigger":{"opened":true},
"valve":{"flowRateResistance":100.0},
"id":{"timestamp":1519597583,"machineIdentifier":2799516,"processIdentifier":64,"counter":12170025,"date":1519597583000,"time":1519597583000,"timeSecond":1519597583}
}
*/
