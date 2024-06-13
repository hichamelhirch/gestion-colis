declare module 'jspdf' {
  export class jsPDF {
    constructor();
    public addImage(imageData: string, format: string, x: number, y: number, width: number, height: number): void;
    public save(filename: string): void;
    public getImageProperties(imageData: string): { width: number; height: number };
    public internal: {
      pageSize: {
        getWidth: () => number;
      };
    };
  }
}
