import { GenericValue } from './generic-value';
import { Trigger } from './trigger';
import { Valve } from './valve';

export class Sector extends GenericValue {
  flowRate: number;
  flowRateCounted: number;

  ownerTankId: number;
  sectorId: number;

  trigger: Trigger;

  static plots = [
    {
      name: "Flow Rate",
      labels: ["Timestamp", "Measured Flow Rate", "Total Flow Rate"],
      y_label: "A",
      toArray: Sector.toArray_1
    },
    {
      name: "Output Enabled\\Disabled",
      labels: ["Timestamp", "Status"],
      y_label: "Boolean",
      toArray: Sector.toArray_4
    },
  ];

  static toArray_1(stat : Sector): any {
    return [ new Date(stat.id.time), stat.flowRateCounted, stat.flowRate ];
  }

  static toArray_4(stat : Sector): any {
    return [ new Date(stat.id.time), (stat.trigger.opened == true) ? 1 : 0 ];
  }
}
