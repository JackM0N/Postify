import { Component, ElementRef, EventEmitter, HostListener, Inject, Input, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-popup-dialog',
  templateUrl: './popup.component.html',
  styleUrls: [
    '/src/app/styles/popup.component.css',
    '/src/app/styles/popup-styles.css'
  ]
})
export class PopupDialogComponent implements OnInit {
  @Input() dialogTitle: string = '';
  @Input() dialogContent: string = '';
  @Input() style: string = '';
  @Input() ngClass: string = '';

  @Input() showCancelButton: boolean = true;
  @Input() cancelButtonLabel: string = 'Anuluj';
  @Input() cancelButtonNgClass: string = '';
  @Output() cancelEvent = new EventEmitter<void>();

  @Input() showConfirmButton: boolean = true;
  @Input() confirmButtonLabel: string = 'Potwierdź';
  @Input() confirmButtonNgClass: string = '';
  @Input() confirmButtonDisabled: boolean = false;
  @Output() confirmEvent = new EventEmitter<void>();

  constructor(
    private dialogRef: MatDialogRef<PopupDialogComponent>,
    private elRef: ElementRef,
    @Inject(MAT_DIALOG_DATA) protected injectedData: any
  ) {}

  ngOnInit(): void {
    if (this.injectedData) {
      this.dialogTitle = this.injectedData.dialogTitle || this.dialogTitle;
      this.dialogContent = this.injectedData.dialogContent || this.dialogContent;
      this.style = this.injectedData.style || this.style;
      this.ngClass = this.injectedData.ngClass || this.ngClass;

      this.showCancelButton = this.injectedData.showCancelButton || this.showCancelButton;
      this.cancelButtonLabel = this.injectedData.cancelButtonLabel || this.cancelButtonLabel;
      this.cancelButtonNgClass = this.injectedData.cancelButtonNgClass || this.cancelButtonNgClass;

      this.showConfirmButton = this.injectedData.showConfirmButton || this.showConfirmButton;
      this.confirmButtonLabel = this.injectedData.confirmButtonLabel || this.confirmButtonLabel;
      this.confirmButtonNgClass = this.injectedData.confirmButtonNgClass || this.confirmButtonNgClass;
      this.confirmButtonDisabled = this.injectedData.confirmButtonDisabled || this.confirmButtonDisabled;
    }
  }

  onConfirm(): void {
    if (!this.confirmButtonDisabled) {
      this.confirmEvent.emit();
      if (this.dialogRef) {
        this.dialogRef.close(true);
      }
    }
  }

  onCancel(): void {
    this.cancelEvent.emit();
    if (this.dialogRef) {
      this.dialogRef.close(false);
    }
  }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent) {
    if (this.dialogRef) {
      const targetElement = event.target as HTMLElement;
      const popupBackground = this.elRef.nativeElement.querySelector('.popup-overlay');
  
      if (targetElement && popupBackground && targetElement == popupBackground) {
        this.onCancel();
      }
    }
  }
}
