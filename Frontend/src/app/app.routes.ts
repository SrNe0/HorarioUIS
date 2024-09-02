import { Routes } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';
import { AsignaturasComponent } from './pages/content/components/asignaturas/asignaturas.component';
import { AulasComponent } from './pages/content/components/aulas/aulas.component';
import { EdificiosComponent } from './pages/content/components/edificios/edificios.component';
import { UsuariosComponent } from './pages/content/components/usuarios/usuarios.component';
import { GruposComponent } from './pages/content/components/grupos/grupos.component';
import { HorarioComponent } from './pages/content/components/horario/horario.component';
import { ProfesoresComponent } from './pages/content/components/profesores/profesores.component';
import { ReportesComponent } from './pages/content/components/reportes/reportes.component';
import { loginGuard } from './guards/login.guard';
import { NewComponent } from './components/new/new.component';
import { ModifyComponent } from './components/modify/modify.component';


export const routes: Routes = [
    {
        path: 'login',
        title: 'Login',
        component: LoginComponent
    },
    {
        path: 'home',
        title: 'Inicio',
        loadComponent: () => import('./pages/home/home.component').then((c) => c.HomeComponent),
        canActivate: [loginGuard]
    },
    {
        path: 'usuario',
        loadComponent: () => import('./pages/content/content.component').then((c) => c.ContentComponent),
        canActivate: [loginGuard],
        canActivateChild: [loginGuard],
        children: [
            {
                path: 'asignaturas',
                title: 'Asignaturas',
                component: AsignaturasComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'aulas',
                title: 'Aulas',
                component: AulasComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'edificios',
                title: 'Edificios',
                component: EdificiosComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'usuarios',
                title: 'Usuarios',
                component: UsuariosComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'grupos',
                title: 'Grupos',
                component: GruposComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'horario',
                title: 'Horario',
                component: HorarioComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
            {
                path: 'profesores',
                title: 'Profesores',
                component: ProfesoresComponent,
                children:[
                    {
                        path: 'nuevo',
                        component: NewComponent
                    },
                    {
                        path: 'modificar',
                        component: ModifyComponent
                    }
                ]
            },
        ]          
        
    },
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: 'login',
        pathMatch: 'full'
    }
];
