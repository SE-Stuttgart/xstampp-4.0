import { shapes, dia } from 'jointjs';

export class ShapeUtils {

    /**
     * controller shape.
     * @param label name of the controller
     */
    ControllerShape: Element = shapes.standard.Rectangle.define('xstampp.ControllerShape', {
        attrs: {
            body: {
                refWidth: '100%',
                refHeight: '100%',
                strokeWidth: 2,
                stroke: '#3700FF',
                fill: 'white'
            },
            label: {
                textVerticalAnchor: 'middle',
                textAnchor: 'middle',
                refX: '50%',
                refY: '12%',
                fontWeight: 'bold',
                fontSize: 13,
                fill: 'black',
            },
            // control algorithm with label
            ca: {
                // cursor: 'pointer',
                refX: '0%',
                refX2: 0,
                refY2: 0,
                refY: '25%',
                strokeWidth: 2,
                stroke: '#3700FF',
                fill: 'green',
                refWidth: '50%',
                refHeight: '75%',
            },
            calabel: {
                textVerticalAnchor: 'middle',
                textAnchor: 'middle',
                refX: '25%',
                refY: '63%',
                refX2: 0,
                refY2: 0,
                fontWeight: 'bold',
                fontSize: 13,
                fill: 'white',
                text: 'CA'
            },
            // proces model rectangle with label
            pm: {
                // cursor: 'pointer',
                refX: '50%',
                refX2: 0,
                refY2: 0,
                refY: '25%',
                strokeWidth: 2,
                stroke: '#3700FF',
                fill: 'PaleTurquoise',
                refWidth: '50%',
                refHeight: '75%'
            },
            pmlabel: {
                textVerticalAnchor: 'middle',
                textAnchor: 'middle',
                refX: '75%',
                refY: '63%',
                refX2: 0,
                refY2: 0,
                fontWeight: 'bold',
                fontSize: 13,
                fill: 'black',
                text: 'PM'
            },
        },
        markup: [
            {
                tagName: 'rect',
                selector: 'body'
            },
            {
                tagName: 'text',
                selector: 'label'
            },
            {
                tagName: 'rect',
                selector: 'dependencies'
            },
            {
                tagName: 'text',
                selector: 'calabel'
            },
            {
                tagName: 'rect',
                selector: 'pm',
            },
            {
                tagName: 'text',
                selector: 'pmlabel'
            },
        ]
    });

    /**
     * process shape
     * @param label name of the process
     */
    ProcessShape: Element = shapes.standard.Rectangle.define('xstampp.ProcessShape', {
        attrs: {
            body: {
                stroke: '#8d0083',
                strokeWidth: 2,
                strokeDasharray: 'bold'
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });

    /**
     * sensor shape
     * @param label name of the sensor
     */
    SensorShape: Element = shapes.standard.Rectangle.define('xstampp.SensorShape', {
        attrs: {
            body: {
                stroke: '#00ff00',
                strokeWidth: 2,
                strokeDasharray: 'bold'
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });

    /**
     * actuator shape
     * @param label name of the actuator
     */
    ActuatorShape: Element = shapes.standard.Rectangle.define('xstampp.ActuatorShape', {
        attrs: {
            body: {
                stroke: '#ffc700',
                strokeWidth: 2,
                strokeDasharray: 'bold'
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });
    DashedBoxShape: Element = shapes.standard.Rectangle.define('xstampp.DashedBox', {
        attrs: {
            body: {
                stroke: '#000000',
                strokeWidth: 5,
                strokeDasharray: 1,
                fill: 'none',
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });
    TextBoxShape: Element = shapes.standard.Rectangle.define('xstampp.TextBox', {
        attrs: {
            body: {
                stroke: '#FFFFFF',
                strokeWidth: 2,
                strokeDasharray: 'bold',
                opacity: 0.5
                // todo default delte if empty? opacity ?
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });
    InputBoxShape: Element = shapes.standard.Rectangle.define('xstampp.InputBox', {
        attrs: {
            body: {
                stroke: '#000000',
                strokeWidth: 2,
                strokeDasharray: 2,
                fill: '#fafafa'
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });
    OutputBoxShape: Element = shapes.standard.Rectangle.define('xstampp.OutputBox', {
        attrs: {
            body: {
                stroke: '#000000',
                strokeWidth: 2,
                strokeDasharray: 2,
                fill: '#fafafa'
            },
            label: {
                fill: 'black',
                fontWeight: 'bold'
            }
        }
    });

    /**
         * defines the graphandels for box resizing.
         */
    GrabHandles: Element = dia.Element.define('xstampp.GrabHandles', {
        attrs: {
            body: {
                refWidth: '100%',
                refHeight: '100%',
                strokeWidth: 2,
                stroke: 'black',
                fill: 'white'
            },
            buttonNE: {
                cursor: 'ne-resize',
                ref: 'buttonLabelNE',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelNE: {
                pointerEvents: 'none',
                refX: '100%',
                refY: '0%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonSE: {
                cursor: 'se-resize',
                ref: 'buttonLabelSE',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelSE: {
                pointerEvents: 'none',
                refX: '100%',
                refY: '100%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonSW: {
                cursor: 'sw-resize',
                ref: 'buttonLabelSW',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelSW: {
                pointerEvents: 'none',
                refX: '0%',
                refY: '100%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonNW: {
                cursor: 'nw-resize',
                ref: 'buttonLabelNW',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelNW: {
                pointerEvents: 'none',
                refX: '0%',
                refY: '0%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            }, buttonCancel: {
                cursor: 'pointer',
                ref: 'buttonLabelCancel',
                refWidth: '150%',
                refHeight: '150%',
                refX: '-25%',
                refY: '-25%'
            },
            buttonLabelCancel: {
                pointerEvents: 'none',
                refX: '50%',
                refY: '50%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonN: {
                cursor: 'n-resize',
                ref: 'buttonLabelN',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelN: {
                pointerEvents: 'none',
                refX: '50%',
                refY: '0%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonS: {
                cursor: 's-resize',
                ref: 'buttonLabelS',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelS: {
                pointerEvents: 'none',
                refX: '50%',
                refY: '100%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonW: {
                cursor: 'w-resize',
                ref: 'buttonLabelW',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelW: {
                pointerEvents: 'none',
                refX: '0%',
                refY: '50%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
            buttonE: {
                cursor: 'e-resize',
                ref: 'buttonLabelE',
                refWidth: '150%',
                refHeight: '50%',
                refX: '0%',
                refY: '+25%'
            },
            buttonLabelE: {
                pointerEvents: 'none',
                refX: '100%',
                refY: '50%',
                textAnchor: 'middle',
                textVerticalAnchor: 'middle'
            },
        }
    }, {
            markup: [{
                tagName: 'rect',
                selector: 'body',
            }, {
                tagName: 'text',
                selector: 'label'
            }, {
                tagName: 'rect',
                selector: 'buttonN'
            }, {
                tagName: 'text',
                selector: 'buttonLabelN'
            }, {
                tagName: 'rect',
                selector: 'buttonE'
            }, {
                tagName: 'text',
                selector: 'buttonLabelE'
            }, {
                tagName: 'rect',
                selector: 'buttonW'
            }, {
                tagName: 'text',
                selector: 'buttonLabelW'
            }, {
                tagName: 'rect',
                selector: 'buttonS'
            }, {
                tagName: 'text',
                selector: 'buttonLabelS'
            }, {
                tagName: 'rect',
                selector: 'buttonNE'
            }, {
                tagName: 'text',
                selector: 'buttonLabelNE'
            }, {
                tagName: 'rect',
                selector: 'buttonSE'
            }, {
                tagName: 'text',
                selector: 'buttonLabelSE'
            }, {
                tagName: 'rect',
                selector: 'buttonSW'
            }, {
                tagName: 'text',
                selector: 'buttonLabelSW'
            }, {
                tagName: 'rect',
                selector: 'buttonNW'
            }, {
                tagName: 'text',
                selector: 'buttonLabelNW'
            }, {
                tagName: 'rect',
                selector: 'buttonCancel'
            }, {
                tagName: 'text',
                selector: 'buttonLabelCancel'
            }]
        });

    getGraph(): dia.Graph {
        return new dia.Graph({}, {
            cellNamespace: {
                shapes,
                xstampp: {
                    ProcessShape: this.ProcessShape,
                    ControllerShape: this.ControllerShape,
                    SensorShape: this.SensorShape,
                    ActuatorShape: this.ActuatorShape,
                    DashedBox: this.DashedBoxShape,
                    TextBox: this.TextBoxShape,
                    InputBox: this.InputBoxShape,
                    OutputBox: this.OutputBoxShape,
                    GrabHandles: this.GrabHandles,
                }
            }
        });
    }

    getShapeByName(shapeName: string): dia.Element {
        switch (shapeName) {
            case 'Controller': {
                return new this.ControllerShape();
            }
            case 'ControlledProcess': {
                return new this.ProcessShape();
            }
            case 'Sensor': {
                return new this.SensorShape();
            }
            case 'Actuator': {
                return new this.ActuatorShape();
            }
            case 'DashedBox': {
                let shape = new this.DashedBoxShape();
                /* Set empty text (needed for JSONify) */
                shape.attr({
                    label: {
                        text: ''
                    }
                });
                return shape;
            }
            case 'TextBox': {
                let shape = new this.TextBoxShape();
                /* Set text (needed for JSONify) */
                shape.attr({
                    label: {
                        text: 'TextBox'
                    }
                });
                return shape;
                break;
            }
            case 'InputBox': {
                let shape = new this.InputBoxShape();
                /* Set text (needed for JSONify) */
                shape.attr({
                    label: {
                        text: 'Input'
                    }
                });
                return shape;
            }
            case 'OutputBox': {
                let shape = new this.OutputBoxShape();
                /* Set text (needed for JSONify) */
                shape.attr({
                    label: {
                        text: 'Output'
                    }
                });
                return shape;
            }
        }
        return null;
    }
}

type Element = dia.Cell.Constructor<dia.Element>;
