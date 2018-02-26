import { ObjectId } from './object-id';

export abstract class GenericValue {
  id: ObjectId;

  className: string;
}
