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

  static plots = [
    {
      name: "Flow Rate",
      labels: ["Timestamp", "Input Flow Rate", "Output Flow Rate"],
      y_label: "A",
      toArray: Tank.toArray_1
    },
    {
      name: "Input Resistance",
      labels: ["Timestamp", "Input Resistance"],
      y_label: "V",
      toArray: Tank.toArray_2
    },
    {
      name: "Pression",
      labels: ["Timestamp", "Pression"],
      y_label: "V",
      toArray: Tank.toArray_3
    },
    {
      name: "Output Enabled\\Disabled",
      labels: ["Timestamp", "Status"],
      y_label: "Boolean",
      toArray: Tank.toArray_4
    },
  ];

  static toArray_1(stat : Tank): any {
    return [ new Date(stat.id.time), stat.inputFlowRate, stat.outputFlowRate ];
  }

  static toArray_2(stat : Tank): any {
    return [ new Date(stat.id.time), stat.valve.flowRateResistance ];
  }

  static toArray_3(stat : Tank): any {
    return [ new Date(stat.id.time), stat.capacity ];
  }

  static toArray_4(stat : Tank): any {
    return [ new Date(stat.id.time), (stat.trigger.opened == true) ? 1 : 0 ];
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
